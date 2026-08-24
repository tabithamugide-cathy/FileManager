package com.servicecop.FileManager.webtoken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {
    //generate token
    private static final String SECRET = "2F3660D9F4E63933369A8DE3A28A53D90D65DDB4901B53D66E439CC41415B7DA4149FAC766FB1F07A8D9266F9C821AD8E4AFC67B3486137FD2AF43961BC99860";
    public static final long VALIDITY = TimeUnit.MINUTES.toMillis(60);
    public String generateToken(UserDetails userDetails){
        Map<String,String> claims = new HashMap<>();
        claims.put("name", "Mugide");

      return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(generateKey())
                .compact();
    }
//    private SecretKey generateKey() {
//        byte[] decodedKey = HexFormat.of().parseHex(SECRET);
//        return Keys.hmacShaKeyFor(decodedKey);
//    }
    private SecretKey generateKey(){
        byte[]decodedKey = Decoders.BASE64.decode(SECRET);
        //byte[] decodedKey = Decoders.HEX.decode(SECRET);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    // Helper method to extract claims (prevents duplicating parser code)
    private Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
    //extract username from jwt
    public String extractUsername(String jwt){
        Claims claims = extractAllClaims(jwt);
        return claims.getSubject();
    }

    public boolean isTokenValid(String jwt){
        Claims claims = extractAllClaims(jwt);
        return claims.getExpiration().after(Date.from(Instant.now()));
    }
}
