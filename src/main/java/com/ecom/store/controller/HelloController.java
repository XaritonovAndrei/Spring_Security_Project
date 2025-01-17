package com.ecom.store.controller;

import com.ecom.store.jwt.JwtUtils;
import com.ecom.store.jwt.LoginRequest;
import com.ecom.store.jwt.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class HelloController {

    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    @GetMapping("/hello")
    public String helloTest() {
        return "Hello";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String userEndpoint() {
        return "Hello, User";
    }

    @GetMapping("/admin")
    public String adminEndpoint() {
        return "Hello, Admin";
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        }
        catch(AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
                map.put("message","bad credentials");
                map.put("status", false);
                return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);

        }
        // добавляет объект аутенфикации в секьюрити контекст
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // отправляет пользователя в userDetails, меняя тип данных
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // токен генерируется, только если пользователь аутенфицирован
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // лист ролей, чтобы передать в респонс
        List<String> roles = userDetails.getAuthorities()
                .stream()
                    .map(item -> item.getAuthority()).collect(Collectors.toList());

        LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken);

        // возвращает респонс со статусом ОК
        return ResponseEntity.ok(response);
    }
}
