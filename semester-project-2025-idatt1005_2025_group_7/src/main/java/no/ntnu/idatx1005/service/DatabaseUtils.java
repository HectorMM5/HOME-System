package no.ntnu.idatx1005.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>DatabaseUtils class</h3>
 *
 * <p>Utility class for database operations with methods for generating salts and 
 * hashing passwords.
 *
 * @author William Holtsdalen
 * @since V0.1.0
 */
public final class DatabaseUtils {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);

  /** Prevents instantiation of the databaseUtils class.*/
  private DatabaseUtils() {}

  /**
   * Generates a salt, for use in hashing passwords.
   *
   * <p>The salt is made up of 16 random bytes, generated using a SecureRandom instance.
   *
   * @see SecureRandom
   * @return A salt.
   */
  public static byte[] generateSalt() {
    logger.debug("Generating new salt for password hashing");
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return salt;
  }

  /**
   * Hashes the given password with the given salt.
   *
   * <p>The implementation of this method is based on the examples in this article:
   * https://www.baeldung.com/java-password-hashing
   *
   * @param password The password to hash.
   * @param salt The salt to use for hashing.
   * @return The hashed password.
   */
  public static byte[] hashPassword(String password, byte[] salt) {
    logger.debug("Hashing password with salt");
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
      messageDigest.update(salt);

      return messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException e) {
      logger.error("Failed to hash password: SHA-512 algorithm not available", e);
    }
    return new byte[0];
  }
}
