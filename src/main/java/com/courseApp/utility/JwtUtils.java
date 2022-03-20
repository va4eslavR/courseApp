package com.courseApp.utility;

import com.courseApp.models.AppUserDetails;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private  Logger logger= LoggerFactory.getLogger(this.getClass());
    @Value("${app.jwtSecret}")
    private String jwtSecret;
    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication auth){
        AppUserDetails appUserDetails=(AppUserDetails) auth.getPrincipal();
        return Jwts.builder()
                .setSubject(appUserDetails.getEmail()) //Change username for unique email field!!
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime()+jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512,jwtSecret)
                .compact();
    }
    public String getUsernameFromToken(String token){
        var body= Jwts.parser().setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        logger.info("got token with username " + body.getSubject()+"/n"+
                "with id: "+body.getId() +"/n"+
                "with email: "+body.get("email",String.class));

        return body.getSubject();
    }
    public boolean validateJwtToken(String token){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        }
        catch (SignatureException e) {
            logger.error("invalid signature in jwtToken "+e.getMessage());}
        catch (MalformedJwtException e){
            logger.error("invalid type of token "+e.getMessage());}
        catch (UnsupportedJwtException e){
            logger.error("jwt token unsupported "+e.getMessage());}
        catch (IllegalArgumentException e){
            logger.error("illegal arg exception "+e.getMessage());
        }
        return false;
    }
}
