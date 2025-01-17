package com.ecom.store.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // секретный ключ
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    // срок действия токена
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // вытаскивает jwt-токен из header'a
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization Header: {}", bearerToken);
        // условие проверяет значение хэдера и срезает "Bearer: ", оставляя только токен
        if (bearerToken != null && bearerToken.startsWith("Bearer: ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // генерирует токен на основании Имени Пользователя, присваиваент ключ
    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    // вытаскивает Имя Пользователя из токена (Subject)
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    // валидатор токена. Ловит ошибки. возвращает false, если не прошла верификация ключа
    public boolean validateJwtToken(String authToken) {

        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build().parseSignedClaims(authToken);
                    return true;
        }
        catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e) {
            logger.error("JWT Token is expired: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e) {
            logger.error("JWT Token is not supported: {}", e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.error("JWT Claims string is empty: {}", e.getMessage());
        }
        return  false;
    }

}
