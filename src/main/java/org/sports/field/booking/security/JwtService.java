package org.sports.field.booking.security;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;

import org.jboss.logging.Logger;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.auth.principal.DefaultJWTParser;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtService {

    private static final Logger LOG = Logger.getLogger(JwtService.class);

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private JWTParser jwtParser;

    public JwtService() {
        this.jwtParser = new DefaultJWTParser();
    }

    // Inisialisasi key saat pertama kali digunakan
    private void initKeys() throws Exception {
        if (privateKey == null) {
            loadKeys();
        }
    }

    private void loadKeys() throws Exception {
        // Load private key
        InputStream privateIs = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("privateKey_pkcs8.pem");

        if (privateIs == null) {
            throw new RuntimeException("privateKey_pkcs8.pem not found in classpath");
        }

        String privateKeyContent = new String(privateIs.readAllBytes(), StandardCharsets.UTF_8);
        this.privateKey = loadPrivateKey(privateKeyContent);

        // Load public key
        InputStream publicIs = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("publicKey.pem");

        if (publicIs == null) {
            throw new RuntimeException("publicKey.pem not found in classpath");
        }

        String publicKeyContent = new String(publicIs.readAllBytes(), StandardCharsets.UTF_8);
        this.publicKey = loadPublicKey(publicKeyContent);
    }

    public String generateToken(String username) throws Exception {
        initKeys();

        return Jwt.issuer("your-issuer")
                .subject(username)
                .claim("token_type", "access")
                .expiresIn(3600) // 1 hour
                .sign(privateKey);
    }

    public String generateRefreshToken(String username) throws Exception {
        initKeys();

        return Jwt.issuer("your-issuer")
                .subject(username)
                .claim("token_type", "refresh")
                .expiresIn(7 * 24 * 3600) // 7 days
                .sign(privateKey);
    }

    public String refreshAccessToken(String refreshToken) throws Exception {
        // Validate refresh token
        TokenValidationResult validationResult = validateToken(refreshToken);

        if (!validationResult.isValid()) {
            throw new RuntimeException("Invalid refresh token: " + validationResult.getErrorMessage());
        }

        // Check if it's a refresh token
        String tokenType = validationResult.getClaim("token_type");
        if (!"refresh".equals(tokenType)) {
            throw new RuntimeException("Invalid token type. Expected refresh token");
        }

        // Generate new access token
        String username = validationResult.getSubject();
        return generateToken(username);
    }

    public TokenValidationResult validateToken(String token) {
        try {
            initKeys();

            // Parse and verify JWT with public key
            var jwt = jwtParser.verify(token, publicKey);

            // Check expiration via claim
            Long exp = jwt.getClaim("exp");
            if (exp != null && Instant.ofEpochSecond(exp).isBefore(Instant.now())) {
                return new TokenValidationResult(false, "Token has expired");
            }

            // Check issuer
            String issuer = jwt.getIssuer();
            if (issuer == null || !"your-issuer".equals(issuer)) {
                return new TokenValidationResult(false, "Invalid issuer");
            }

            // Token is valid
            TokenValidationResult result = new TokenValidationResult(true, null);
            result.setSubject(jwt.getName());
            result.setClaim("token_type", jwt.getClaim("token_type"));

            return result;

        } catch (Exception e) {
            LOG.error("Failed to validate JWT: " + e.getMessage(), e);
            return new TokenValidationResult(false, "Token validation failed: " + e.getMessage());
        }
    }

    public boolean isTokenValid(String token) {
        return validateToken(token).isValid();
    }

    public String extractUsername(String token) throws Exception {
        TokenValidationResult result = validateToken(token);
        if (!result.isValid()) {
            throw new RuntimeException("Invalid token");
        }
        return result.getSubject();
    }

    public String extractTokenType(String token) throws Exception {
        TokenValidationResult result = validateToken(token);
        if (!result.isValid()) {
            throw new RuntimeException("Invalid token");
        }
        return result.getClaim("token_type");
    }

    private PrivateKey loadPrivateKey(String pemContent) throws Exception {
        // Remove PEM headers and footers
        String privateKeyPEM = pemContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        // Decode Base64
        byte[] encoded;
        try {
            encoded = Base64.getDecoder().decode(privateKeyPEM);
        } catch (IllegalArgumentException e) {
            LOG.error("Failed to decode private key", e);
            throw new RuntimeException("Invalid private key format", e);
        }

        // Generate PrivateKey
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private PublicKey loadPublicKey(String pemContent) throws Exception {
        // Remove PEM headers and footers
        String publicKeyPEM = pemContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        // Decode Base64
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        // Generate PublicKey
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}