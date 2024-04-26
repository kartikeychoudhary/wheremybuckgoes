package kc.wheremybuckgoes.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JWTHelper {

    private JWTHelper() {
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token, String secretKey) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static SecretKey getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static boolean isTokenExpired(String token, String secretKey) {
        return extractExpiration(token, secretKey).before(new Date());
    }

    public static Date extractExpiration(String token, String secretKey) {
        return extractClaim(token, Claims::getExpiration, secretKey);
    }
}
