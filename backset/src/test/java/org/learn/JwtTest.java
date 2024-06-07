package org.learn;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
    public void testGen(){
        Map<String,Object> claims = new HashMap<>();
        claims.put("id","1");
        claims.put("name","ni");

        //生成JWT
        String token = JWT.create()
                .withClaim("user",claims)
                .withExpiresAt(new Date(System.currentTimeMillis()+1000*60*60*12))
                .sign(Algorithm.HMAC256("watersqy"));

        System.out.println(token);
    }

    @Test
    public void testParse(){
        String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"+
                ".eyJ1c2VyIjp7Im5hbWUiOiJuaSIsImlkIjoiMSJ9LCJleHAiOjE3MTY1MzMyNjB9"+
                ".AYUCaFwj6DL9vuIWY7Q1DznTuE4u-g5hXVflOHJeYUI";
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("watersqy")).build();

        DecodedJWT decodedJWT = jwtVerifier.verify(token);//验证token，生成解析后的JWT对象
        Map<String, Claim> claims= decodedJWT.getClaims();
        System.out.println(claims.get("user"));
    }
}
