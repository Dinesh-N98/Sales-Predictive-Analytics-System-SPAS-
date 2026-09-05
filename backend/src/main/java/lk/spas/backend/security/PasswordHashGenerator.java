package lk.spas.backend.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordHashGenerator {

    public static void main(String[] args) {
        if (args.length != 1 || args[0] == null || args[0].isBlank()) {
            System.out.println("Usage: PasswordHashGenerator <plaintext-password>");
            return;
        }
        String plaintext = args[0];

        String hash = hashPassword(plaintext);
        System.out.println(hash);
    }

    public static String hashPassword(String plaintext) {
        return BCrypt.withDefaults().hashToString(12, plaintext.toCharArray());
    }
}