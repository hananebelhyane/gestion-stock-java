package com.gestiondestock.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Algorithm algorithm;

    public JwtService(@Value("${security.jwt.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generateToken(String username, String role, long expiresInSeconds) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(expiresInSeconds)))
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) {
        return JWT.require(algorithm).build().verify(token);
    }
}
