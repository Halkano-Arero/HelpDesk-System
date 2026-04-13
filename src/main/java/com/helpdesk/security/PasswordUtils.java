package com.helpdesk.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtils {

    private static final int SALT_BYTES = 16;
    private static final int ITERATIONS = 65_536;
    private static final int KEY_LENGTH = 256;
    private static final String PREFIX = "pbkdf2$";

    private PasswordUtils() {
    }

    public static String hash(String rawPassword) {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        byte[] hash = generateHash(rawPassword.toCharArray(), salt);
        return PREFIX + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }

        if (!storedPassword.startsWith(PREFIX)) {
            return storedPassword.equals(rawPassword);
        }

        String[] parts = storedPassword.split("\\$");
        int iterations = Integer.parseInt(parts[1]);
        byte[] salt = Base64.getDecoder().decode(parts[2]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
        byte[] actualHash = generateHash(rawPassword.toCharArray(), salt, iterations);
        return constantTimeEquals(expectedHash, actualHash);
    }

    public static boolean isLegacyPlaintext(String storedPassword) {
        return storedPassword != null && !storedPassword.startsWith(PREFIX);
    }

    private static byte[] generateHash(char[] password, byte[] salt) {
        return generateHash(password, salt, ITERATIONS);
    }

    private static byte[] generateHash(char[] password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Unable to hash password.", e);
        }
    }

    private static boolean constantTimeEquals(byte[] left, byte[] right) {
        if (left.length != right.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < left.length; i++) {
            result |= left[i] ^ right[i];
        }
        return result == 0;
    }
}
