package com.danahub.zipitda.common.util;

import com.danahub.zipitda.common.exception.ErrorType;
import com.danahub.zipitda.common.exception.ZipitdaException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final UserDetailsService userDetailsService;
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30분
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일
    private SecretKey secretKey;

    @Value("${jwt.secret}")
    public void setSecretKey(String secret) {
        log.info("JWT SecretKey 설정 완료");
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    public String generateAccessToken(String email, String role) {
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        log.info("Access Token 생성: {}", token);
        return token;
    }

    public String generateRefreshToken(String email) {
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        log.info("Refresh Token 생성: {}", token);
        return token;
    }

    // JWT 파싱하여 Claims 반환
    public Claims parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.info("JWT 파싱 성공: {}", claims);
            return claims;
        } catch (ExpiredJwtException e) {
            throw new ZipitdaException(ErrorType.TOKEN_EXPIRED, Map.of("token", token), log::warn, e);
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new ZipitdaException(ErrorType.INVALID_TOKEN, Map.of("token", token), log::warn, e);
        }
    }


    // JWT 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new ZipitdaException(ErrorType.TOKEN_EXPIRED, Map.of("만료된 token", token), log::warn, e);
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new ZipitdaException(ErrorType.TOKEN_EXPIRED, Map.of("유효하지 않은 token", token), log::warn, e);
        }
    }

    // Spring Security Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        try {
            Claims claims = parseToken(token);
            String email = claims.getSubject();

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            log.info("인증 객체 생성 완료: {}, role: {}", email);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } catch (Exception e) {
            throw new ZipitdaException(ErrorType.INVALID_TOKEN, log::warn, e);
        }
    }

    // accessToken으로 email 찾기
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey) // 서명에 사용할 시크릿 키
                .parseClaimsJws(token)    // 토큰을 파싱하여 Claims 추출
                .getBody();

        return claims.getSubject();
    }
}