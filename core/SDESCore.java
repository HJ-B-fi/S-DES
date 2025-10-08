package core;

import utils.CommonUtils;

/**
 * S-DES算法核心类
 * 整合了密钥生成、加密和解密功能
 */
public class SDESCore {
    private KeyGenerator keyGenerator;
    private Encryption encryption;
    private Decryption decryption;

    public SDESCore() {
        this.keyGenerator = new KeyGenerator();
        this.encryption = new Encryption();
        this.decryption = new Decryption();
    }

    /**
     * 加密8位二进制数据
     * 
     * @param plaintext 8位二进制明文
     * @param key       10位二进制密钥
     * @return 8位二进制密文
     */
    public String encrypt(String plaintext, String key) {
        String[] keys = keyGenerator.generateKeys(key);
        return encryption.encrypt(plaintext, keys[0], keys[1]);
    }

    /**
     * 解密8位二进制数据
     * 
     * @param ciphertext 8位二进制密文
     * @param key        10位二进制密钥
     * @return 8位二进制明文
     */
    public String decrypt(String ciphertext, String key) {
        String[] keys = keyGenerator.generateKeys(key);
        return decryption.decrypt(ciphertext, keys[1], keys[0]);
    }

    /**
     * 加密ASCII字符串
     * 
     * @param text ASCII明文
     * @param key  10位二进制密钥
     * @return ASCII密文
     */
    public String encryptASCII(String text, String key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            String binaryChar = CommonUtils.charToBinaryString(character, 8);
            String encryptedBinary = encrypt(binaryChar, key);
            char encryptedChar = CommonUtils.binaryStringToChar(encryptedBinary);
            result.append(encryptedChar);
        }
        return result.toString();
    }

    /**
     * 解密ASCII字符串
     * 
     * @param text ASCII密文
     * @param key  10位二进制密钥
     * @return ASCII明文
     */
    public String decryptASCII(String text, String key) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            String binaryChar = CommonUtils.charToBinaryString(character, 8);
            String decryptedBinary = decrypt(binaryChar, key);
            char decryptedChar = CommonUtils.binaryStringToChar(decryptedBinary);
            result.append(decryptedChar);
        }
        return result.toString();
    }
}