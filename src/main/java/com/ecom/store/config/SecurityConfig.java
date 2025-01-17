package com.ecom.store.config;

import com.ecom.store.jwt.AuthEntryPointJwt;
import com.ecom.store.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    DataSource dataSource;

    // объект метода для обработки неаутенфицированного доступа
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // переххватывает реквест для проверки jwt в header'е
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }


    // signin не требует аутенфикации
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests.requestMatchers("/hello", "/signin", "/error")
                .permitAll()
                .anyRequest()
                .authenticated());

        // убирает куки и делает сессию стейтлесс для работы jwt
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // обработки исключений моим кастомным энтри понитом AuthEntryPointJwt,
        //      основанном на встроенном AuthenticationEntryPoint
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));

        // это нужно только для h2 консоли?
        http.headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()));
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.disable());

        // authenticationJwtTokenFilter - объект моего кастомного фильтра AuthTokenFilter,
        //          основанного на встроенном OncePerRequestFilter
        // чтобы мой фильтр authenticationJwtTokenFilter в цепочке фильтров
        //          срабатывал перед встроенным UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    // для приоритета при запуске инициализации контекста
    // можно использовать ApplicationRunner
    // можно использовать @Component, но тогда нужно использовать implements
    @Bean
    public CommandLineRunner initializeData(UserDetailsService userDetailsService) {
        return args -> {
            JdbcUserDetailsManager manager = (JdbcUserDetailsManager) userDetailsService;
            UserDetails user1 = User.withUsername("user5")
                    .password(passwordEncoder().encode("password5"))
                    .roles("USER")
                    .build();

            UserDetails admin = User.withUsername("admin5")
                    .password(passwordEncoder().encode("adminpw5"))
                    .roles("ADMIN")
                    .build();
            JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
            userDetailsManager.createUser(user1);
            userDetailsManager.createUser(admin);
        };
    }


/**
    // скопировал из SpringBootWebSecurityConfiguration
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
        http.csrf(csrf -> csrf.disable());
        // убирает куки и делает сессию стейтлесс
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        // создает объект UserDetails и передаёт ему след. параметры
        UserDetails user1 = User.withUsername("user4")
                .password(passwordEncoder().encode("password4"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin3")
                .password(passwordEncoder().encode("adminpw4"))
                .roles("ADMIN")
                .build();


        // InMemoryUserDetailsManager создаёт объекты в памяти
        // JdbcUserDetailsManager создаёт объекты в БД
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.createUser(user1);
        userDetailsManager.createUser(admin);
        return userDetailsManager;

//         вызов конструктора, который управляет деталями юзера в памяти
//         создаёт и возвращает объект UserDetails (user1 и admin)
//        return new InMemoryUserDetailsManager(user1, admin);
    }

*/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // раскрыавет менеджер как бин для спринга для управления аутенфикацией
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder)
        throws Exception {
        return builder.getAuthenticationManager();
    }

}
