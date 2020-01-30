package com.notes.service;


import com.notes.exception.InvalidJwtAuthenticationException;
import com.notes.model.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class JwtServiceImpl implements JwtService {

    private String secret;
    private int sessionTime;

    public JwtServiceImpl(@Value("${jwt.secret}") String secret,
                          @Value("${jwt.sessionTime}") int sessionTime){
        this.secret=secret;
        this.sessionTime=sessionTime;

    }

    public String toToken(User user){
        return Jwts.builder()
                .setSubject(user.getId())
                .setExpiration(this.expireTimeFromNow())
                .signWith(SignatureAlgorithm.HS512,this.secret)
                .compact();
    }

    public Optional<String> getSubjectFromToken(String token){
        try{

            Jws<Claims> claimsJws=Jwts.parser()
                    .setSigningKey(this.secret)
                    .parseClaimsJws(token);

            if (claimsJws.getBody().getExpiration().before(new Date())) {
                throw new JwtException("Expired JWT token");
            }

            return Optional.ofNullable(claimsJws.getBody().getSubject());

        }catch(JwtException | IllegalArgumentException e){
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }

    }

    private Date expireTimeFromNow(){
        return new Date(
            System.currentTimeMillis()+this.sessionTime*1000
        );
    }
}
