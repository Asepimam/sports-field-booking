package org.sports.field.booking.infrastructure.security;

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

import org.sports.field.booking.application.security.TokenService;
import org.sports.field.booking.application.security.TokenValidationResult;
import org.sports.field.booking.domain.entity.Role;
import org.jboss.logging.Logger;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.auth.principal.DefaultJWTParser;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtService implements TokenService {

    private static final Logger LOG = Logger.getLogger(JwtService.class);

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private JWTParser jwtParser;
    private JWTAuthContextInfo authContextInfo;

    public JwtService() {
        loadKeys();
        // Initialize parser with public key for verification
        this.authContextInfo = new JWTAuthContextInfo(publicKey, "your-issuer");
        this.jwtParser = new DefaultJWTParser();
    }

    private void loadKeys() {
        try {
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

            LOG.info("JWT keys loaded successfully");
        } catch (Exception e) {
            LOG.error("Failed to load JWT keys: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize JWT keys", e);
        }
    }

    @Override
    public String generateToken(String username, Role role) throws Exception {
        // Pastikan issuer MATCH dengan konfigurasi di application.properties
        String issuer = "your-issuer"; // Harus sama dengan di application.properties

        return Jwt.issuer(issuer)
                .subject(username)
                .claim("token_type", "access")
                .groups(Set.of(role.name()))
                .expiresIn(3600)
                .sign(privateKey);
    }

    @Override
    public String generateToken(String username) throws Exception {
        return generateToken(username, Role.CUSTOMER);
    }

    @Override
    public String generateRefreshToken(String username) throws Exception {
        return Jwt.issuer("your-issuer")
                .subject(username)
                .claim("token_type", "refresh")
                .expiresIn(7 * 24 * 3600)
                .sign(privateKey);
    }

    @Override
    public String refreshAccessToken(String refreshToken) throws Exception {
        TokenValidationResult validationResult = validateToken(refreshToken);

        if (!validationResult.isValid()) {
            throw new RuntimeException("Invalid refresh token: " + validationResult.getErrorMessage());
        }

        String tokenType = validationResult.getClaim("token_type");
        if (!"refresh".equals(tokenType)) {
            throw new RuntimeException("Invalid token type. Expected refresh token");
        }

        String username = validationResult.getSubject();
        return generateToken(username);
    }

    @Override
    public TokenValidationResult validateToken(String token) {
        try {
            // VERIFY token with public key
            var jwt = jwtParser.parse(token, authContextInfo); // ← Ini benar, verifikasi signature

            // Check expiration
            Long exp = jwt.getClaim("exp");
            if (exp != null && Instant.ofEpochSecond(exp).isBefore(Instant.now())) {
                return new TokenValidationResult(false, "Token has expired");
            }

            TokenValidationResult result = new TokenValidationResult(true, null);
            result.setSubject(jwt.getName());

            // Get claims
            Object tokenTypeClaim = jwt.getClaim("token_type");
            Object groupsClaim = jwt.getClaim("groups");

            result.setClaim("token_type", tokenTypeClaim != null ? tokenTypeClaim.toString() : null);
            result.setClaim("groups", groupsClaim);

            LOG.debug("Token validated successfully for user: " + jwt.getName());

            return result;

        } catch (ParseException e) {
            LOG.error("Failed to validate JWT: " + e.getMessage(), e);
            return new TokenValidationResult(false, "Token validation failed: " + e.getMessage());
        } catch (Exception e) {
            LOG.error("Failed to validate JWT: " + e.getMessage(), e);
            return new TokenValidationResult(false, "Token validation failed: " + e.getMessage());
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        return validateToken(token).isValid();
    }

    @Override
    public String extractUsername(String token) throws Exception {
        TokenValidationResult result = validateToken(token);
        if (!result.isValid()) {
            throw new RuntimeException("Invalid token");
        }
        return result.getSubject();
    }

    @Override
    public String extractTokenType(String token) throws Exception {
        TokenValidationResult result = validateToken(token);
        if (!result.isValid()) {
            throw new RuntimeException("Invalid token");
        }
        return result.getClaim("token_type");
    }

    private PrivateKey loadPrivateKey(String pemContent) throws Exception {
        String privateKeyPEM = pemContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private PublicKey loadPublicKey(String pemContent) throws Exception {
        String publicKeyPEM = pemContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}