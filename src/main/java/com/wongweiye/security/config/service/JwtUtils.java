package com.wongweiye.security.config.service;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.wongweiye.security.RSAKeyProperties;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    // for WebSecurityConfigRS256
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${springRestJWT.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private final JwtEncoder jwtEncoder;

    private RSAKeyProperties rsaKeyProperties;

    public JwtUtils(JwtEncoder jwtEncoder, RSAKeyProperties rsaKeyProperties) {
        this.jwtEncoder = jwtEncoder;
        this.rsaKeyProperties = rsaKeyProperties;
    }

    //now RS256 Algorithm throw Base64-encoded key bytes may only be specified for HMAC signatures.  If using RSA or Elliptic Curve, use the signWith(SignatureAlgorithm, Key) method instead.
    //and HS512 generated token use in authenticated required endpoints will return 401 unauthorized
    //RS256 is Asymmetric, use two key, public key and private key on both side
    //HS512 is Symmetric, use one secret on both side
    public String generateJwtToken(Authentication authentication) {

        // Below both method can generate token as mostly same, just choose the one we want to use
//        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
//
//        Instant now = Instant.now();
//        String scope = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(" "));
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuer("self")
//                .issuedAt(now)
//                .expiresAt(now.plus(1, ChronoUnit.HOURS))
//                .subject(userPrincipal.getUsername())
//                .claim("scope", scope)
//                .claim("role", scope)
//                .build();
//
//        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();


        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .claim("scope",scope)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.RS256, rsaKeyProperties.privateKey())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(rsaKeyProperties.publicKey()).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(rsaKeyProperties.publicKey()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
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

 //---------------for WebSecurityConfigHS256--------------------------------------------
//    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
//
//    @Value("${springRestJWT.app.jwtSecret}")
//    private String jwtSecret;
//
//    @Value("${springRestJWT.app.jwtExpirationMs}")
//    private int jwtExpirationMs;
//
//    private RSAKeyProperties rsaKeyProperties;
//
//    public JwtUtils(RSAKeyProperties rsaKeyProperties) {
//        this.rsaKeyProperties = rsaKeyProperties;
//    }
//
//
//    public String generateJwtToken(Authentication authentication) {
////         now RS256 Algorithm throw Base64-encoded key bytes may only be specified for HMAC signatures.  If using RSA or Elliptic Curve, use the signWith(SignatureAlgorithm, Key) method instead.
////         and HS512 generated token use in authenticated required endpoints will return 401 unauthorized
//        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
//        return Jwts.builder()
//                .setSubject((userPrincipal.getUsername()))
//                .setIssuedAt(new Date())
//                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
//                .signWith(SignatureAlgorithm.HS256, jwtSecret)
//                .compact();
//    }
//
//    public String getUserNameFromJwtToken(String token) {
//        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
//    }
//
//    public boolean validateJwtToken(String authToken) {
//        try {
//            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
//            return true;
//        } catch (SignatureException e) {
//            logger.error("Invalid JWT signature: {}", e.getMessage());
//        } catch (MalformedJwtException e) {
//            logger.error("Invalid JWT token: {}", e.getMessage());
//        } catch (ExpiredJwtException e) {
//            logger.error("JWT token is expired: {}", e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            logger.error("JWT token is unsupported: {}", e.getMessage());
//        } catch (IllegalArgumentException e) {
//            logger.error("JWT claims string is empty: {}", e.getMessage());
//        }
//
//        return false;
//    }


}
