package com.example.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

@Service
public class JwtService {
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    public JwtService(
        JwtProperties jwtProperties,
        ObjectMapper objectMapper
    ) {
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
    }

    public String generateToken(String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(jwtProperties.expirationSeconds());

        return createToken(
            Map.of(
                "alg", "HS256",
                "typ", "JWT"
            ),
            Map.of(
                "sub", email,
                "iat", now.getEpochSecond(),
                "exp", expiresAt.getEpochSecond()
            )
        );
    }

    public Instant getExpiresAt(String token) {
        return Instant.ofEpochSecond(getLongClaim(token, "exp"));
    }

    public String extractEmail(String token) {
        return getStringClaim(token, "sub");
    }

    public boolean isValid(
        String token,
        UserDetails userDetails
    ) {
        String email = extractEmail(token);

        return email != null
            && email.equals(userDetails.getUsername())
            && !isExpired(token)
            && hasValidSignature(token);
    }

    private String createToken(
        Map<String, Object> header,
        Map<String, Object> payload
    ) {
        try {
            String encodedHeader =
                base64UrlEncode(objectMapper.writeValueAsBytes(header));
            String encodedPayload =
                base64UrlEncode(objectMapper.writeValueAsBytes(payload));
            String signingInput = encodedHeader + "." + encodedPayload;
            String signature =
                base64UrlEncode(hmacSha256(signingInput));

            return signingInput + "." + signature;
        } catch (Exception exception) {
            throw new IllegalStateException("Không thể tạo JWT", exception);
        }
    }

    private boolean isExpired(String token) {
        return Instant.now().isAfter(getExpiresAt(token));
    }

    private boolean hasValidSignature(String token) {
        String[] parts = splitToken(token);
        String signingInput = parts[0] + "." + parts[1];
        String expectedSignature =
            base64UrlEncode(hmacSha256(signingInput));

        return constantTimeEquals(expectedSignature, parts[2]);
    }

    private String getStringClaim(String token, String claimName) {
        Object value = getPayload(token).get(claimName);
        return value == null ? null : value.toString();
    }

    private long getLongClaim(String token, String claimName) {
        Object value = getPayload(token).get(claimName);
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getPayload(String token) {
        try {
            String[] parts = splitToken(token);
            byte[] payloadJson =
                Base64.getUrlDecoder().decode(parts[1]);

            return objectMapper.readValue(
                payloadJson,
                Map.class
            );
        } catch (Exception exception) {
            throw new IllegalArgumentException("JWT không hợp lệ", exception);
        }
    }

    private String[] splitToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("JWT phải có 3 phần");
        }
        return parts;
    }

    private byte[] hmacSha256(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM
            );
            mac.init(key);
            return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("Không thể ký JWT", exception);
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);
    }

    private boolean constantTimeEquals(String left, String right) {
        return MessageDigestHelper.equals(
            left.getBytes(StandardCharsets.UTF_8),
            right.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static final class MessageDigestHelper {
        private static boolean equals(byte[] left, byte[] right) {
            return java.security.MessageDigest.isEqual(left, right);
        }
    }
}
