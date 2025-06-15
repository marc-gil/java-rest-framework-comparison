package dev.marcgil.vanilla.security;

import javax.crypto.SecretKey;

public interface SecretKeyProvider {

  SecretKey getSecretKey();

}
