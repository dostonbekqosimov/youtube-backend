package dasturlash.uz.util;

import dasturlash.uz.dto.JwtDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

//    private static final long tokenLiveTime = 1000 * 3600 * 24; // 1-day
//    private static final long refreshTokenLiveTime = 1000L * 3600 * 24 * 30;


    private static final long tokenLiveTime = 1000L * 60 * 60 * 24 * 7; // 1 week (in milliseconds)
    private static final long refreshTokenLiveTime = 1000L * 60 * 60 * 24 * 30; // 1 month (in milliseconds)
    private static final String secretKey = "veryLongSecretmazgillattayevlasharaaxmojonjinnijonsurbetbekkiydirhonuxlatdibekloxovdangasabekochkozjonduxovmashaynikmaydagapchishularnioqiganbolsangizgapyoqaniqsizmazgi";

    public static String encode(String login, String role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role);
        extraClaims.put("login", login);


        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(login)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenLiveTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String refreshToken(String login, String role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role);
        extraClaims.put("login", login);


        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(login)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenLiveTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static TokenValidationResult validateToken(String token) {
        try {
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            Date now = new Date();

            if (expiration.before(now)) {
                return new TokenValidationResult(false, "Token has expired");
            }

            return new TokenValidationResult(true, "Token is valid");

        } catch (ExpiredJwtException e) {
            return new TokenValidationResult(false, "Token has expired");
        } catch (MalformedJwtException e) {
            return new TokenValidationResult(false, "Invalid token format");
        } catch (Exception e) {
            return new TokenValidationResult(false, "Invalid token");
        }
    }

    public static class TokenValidationResult {
        private final boolean valid;
        private final String message;

        public TokenValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

    public static JwtDTO decode(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        String login = (String) claims.get("login");
        String role = (String) claims.get("role");

        return new JwtDTO(login, role);
    }

    private static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
