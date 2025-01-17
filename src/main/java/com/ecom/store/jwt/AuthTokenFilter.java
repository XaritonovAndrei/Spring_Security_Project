package com.ecom.store.jwt;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {
        logger.debug("AuthTokenFilter called for URL {}", request.getRequestURL());
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Authorities - роль пользователя (USER/ADMIN и тд), обозначены в SecurityConfig
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());
                logger.debug("Roles from JWT: {}", userDetails.getAuthorities());

                // создает и наполняет объект authenticationToken доп. параметрами (например, session id), полученными из request
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // отправляет объект authenticationToken в контекст секьюрити, произволдится аутенфикация
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
        }
        catch (Exception e) {
            logger.error("error: {}", e);
        }
        // продолжает цепочку фильтров
        filterChain.doFilter(request, response);
    }

    // парсер. создаёт объект jwt из header'a request'a
    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromHeader(request);
        logger.debug("AUth Token Filter: {}", jwt);
        return jwt;
    }

}
