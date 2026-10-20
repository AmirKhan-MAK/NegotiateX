 package com.negotiatex.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

	@Value("${app.jwt.secret:defaultSecretKeyWithAtLeast256BitsSoItIsSecureEnoughForHMACSHA256_NegotiateX}")
	private String secret;

	@Value("${app.jwt.expirationMs:86400000}") // Default 1 day
	private long jwtExpirationMs;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String generateToken(String email) {
		return Jwts.builder().subject(email).issuedAt(new Date())
				.expiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(getSigningKey()).compact();
	}

	public String getEmailFromToken(String token) {
		Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			System.err.println("Invalid JWT token: " + e.getMessage());
		}
		return false;
	}
}
