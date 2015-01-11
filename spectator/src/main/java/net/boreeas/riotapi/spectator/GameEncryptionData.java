/*
 * Copyright 2014 The LolDevs team (https://github.com/loldevs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi.spectator;

import lombok.Getter;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

/**
 * Created on 8/10/2014.
 */
public class GameEncryptionData {
    @Getter private byte[] key;

    public GameEncryptionData(byte[] key) {
        this.key = key;
    }

    /**
     * Creates a cipher for decrypting data with the specified key.
     * @return A Blowfish/ECB/PKCS5Padding cipher in decryption mode.
     * @throws GeneralSecurityException
     */
    public Cipher getCipher() throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "Blowfish"));

        return cipher;
    }

    /**
     * Creates a cipher for encrypting data with the specified key.
     * @return A Blowfish/ECB/PKCS5Padding cipher in encryption mode.
     * @throws GeneralSecurityException
     */
    public Cipher getEncryptionCipher() throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "Blowfish"));

        return cipher;
    }

    /**
     * Decrypts the data with the specified key.
     * @param in The data to decrypt.
     * @return The decrypted data.
     */
    @SneakyThrows
    public byte[] decrypt(byte[] in) {
        return getCipher().doFinal(in);
    }

    /**
     * Encrypts the data with the specified key.
     * @param in The data to encrypt.
     * @return The encrypted data.
     */
    @SneakyThrows
    public byte[] encrypt(byte[] in) {
        return getEncryptionCipher().doFinal(in);
    }
}
