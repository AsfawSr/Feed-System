package com.asfaw.feed_system.auth.service;

import com.asfaw.feed_system.auth.config.JwtProperties;
import com.asfaw.feed_system.user.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private final JwtProperties jwtProperties;
	private final SecretKey signingKey;

	public JwtService(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(UserAccount user) {
		Instant now = Instant.now();
		Instant expiration = now.plusSeconds(accessTokenExpirationSeconds());

		return Jwts.builder()
				.issuer(jwtProperties.issuer())
				.subject(user.getUsername())
				.claim("userId", user.getId())
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiration))
				.signWith(signingKey)
				.compact();
	}

	public String extractUsername(String token) {
		try {
			return claims(token).getSubject();
		} catch (RuntimeException ex) {
			return null;
		}
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username != null
				&& username.equals(userDetails.getUsername())
				&& claims(token).getExpiration().after(new Date());
	}

	public long accessTokenExpirationSeconds() {
		return jwtProperties.accessTokenExpirationMinutes() * 60;
	}

	private Claims claims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.requireIssuer(jwtProperties.issuer())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
