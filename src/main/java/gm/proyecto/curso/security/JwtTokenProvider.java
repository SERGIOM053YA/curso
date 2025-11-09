package gm.proyecto.curso.security; // Asegúrate que el paquete sea el correcto

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
public class JwtTokenProvider {

    private final String jwtSecret = "MiClaveSecretaSuperLargaYComplejaParaElProyectoDeTitulacion";
    private final long jwtExpirationInMs = 3600000;
    private final Key signingKey;

    // --- CONSTRUCTOR NUEVO ---
    // Preparamos la llave de forma segura
    public JwtTokenProvider() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret.getBytes());
        this.signingKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(signingKey) // <-- Usamos la llave segura
                .compact();
    }

    // --- MÉTODO CORREGIDO ---
    public String getEmailFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey) // <-- Usamos la llave segura
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // --- MÉTODO CORREGIDO ---
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception ex) {
            // No hacemos nada, simplemente no es válido
        }
        return false;
    }
}