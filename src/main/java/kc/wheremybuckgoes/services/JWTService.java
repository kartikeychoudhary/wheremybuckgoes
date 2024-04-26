package kc.wheremybuckgoes.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kc.wheremybuckgoes.utils.JWTHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class JWTService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    public long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    public long refreshExpiration;

    public String getUserEmailFromJWT(String token) {
        return JWTHelper.extractClaim(token, Claims::getSubject, secretKey);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = getUserEmailFromJWT(token);
        return (username.equals(userDetails.getUsername()) && !JWTHelper.isTokenExpired(token, secretKey));
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return buildToken(extraClaims, userDetails,  jwtExpiration);
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(JWTHelper.getSignInKey(secretKey))
                .compact();
    }

}
