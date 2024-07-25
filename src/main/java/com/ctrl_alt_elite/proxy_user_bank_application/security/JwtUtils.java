package com.ctrl_alt_elite.proxy_user_bank_application.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.ctrl_alt_elite.proxy_user_bank_application.service.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Value("${app.jwtCookieName}")
	private String jwtCookie;

	public String getJwtFromCookies(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, jwtCookie);
		if (cookie != null) {
			return cookie.getValue();
		} else {
			return null;
		}
	}

	public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
		String jwt = generateTokenFromEmail(userPrincipal.getEmail());
		return ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(jwtExpirationMs).httpOnly(true)
				.build();
	}

	public ResponseCookie generateJwtCookie(UserDetailsImpl targettedUser, String actualUser) {
		String jwt = generateProxyTokenForEmail(targettedUser.getEmail(), actualUser);
		return ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(jwtExpirationMs).httpOnly(true)
				.build();
	}
	
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
	}

	public ResponseCookie generateJwtCookieProxyEnabled(String token) {
		boolean proxyEnabled = Objects.nonNull(getImpersonatedEmailFromJwtToken(token));
		return ResponseCookie.from("proxyEnabled", String.valueOf(proxyEnabled)).path("/api")
				.maxAge(jwtExpirationMs).httpOnly(true).build();
	}
	
	public boolean isProxyEnabled(String token) {
		return Objects.nonNull(getImpersonatedEmailFromJwtToken(token));
	}

	public ResponseCookie getCleanJwtCookie() {
		ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
		return cookie;
	}

	public String getImpersonatedEmailFromJwtToken(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
		return (String) claims.get("impersonatedUserEmail");
	}

	public String getActualEmailFromJwtToken(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
		return (String) claims.get("actualUserEmail");
	}

	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	public String generateTokenFromEmail(String email) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("actualUserEmail", email);

		return Jwts.builder().setClaims(claims).setSubject(email).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key(), SignatureAlgorithm.HS256).compact();
	}

	public String generateProxyTokenForEmail(String targetEmail, String originalEmail) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("impersonatedUserEmail", targetEmail);
		claims.put("actualUserEmail", originalEmail);
		return Jwts.builder().setClaims(claims).setSubject(targetEmail).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key(), SignatureAlgorithm.HS256).compact();
	}
}
