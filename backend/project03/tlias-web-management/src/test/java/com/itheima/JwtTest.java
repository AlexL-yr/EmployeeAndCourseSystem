package com.itheima;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import io.jsonwebtoken.Jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    @Test
    public void testGenerateJwt(){
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("id",1);
        dataMap.put("username","admin");
        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS256,"WWlSb25nTHU=")
                .addClaims(dataMap)
                .setExpiration(new Date(System.currentTimeMillis()+3600*1000))
                .compact();
        System.out.println(jwt);
    }
    @Test
    public void testParseJwt(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJhZG1pbiIsImV4cCI6MTc1OTY3NTcyOH0.EgL4LbE6zB53OMz_Lpr-blCLLXO_Bpt6u4Yh7xqMCSQ";
        Claims claims = Jwts.parser().setSigningKey("WWlSb25nTHU=")
                .parseClaimsJws(token)
                .getBody();
        System.out.println(claims);
    }
}
