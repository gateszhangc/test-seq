package com.dap.sequence.client.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaEncryptionUtil {
    private static Logger logger = LoggerFactory.getLogger(RsaEncryptionUtil.class);

    /**
     * 获取rsa密码加密字符串
     * @param publicKey
     * @param password
     * @return
     */
    public static String getRsaEncryptionPassword(String publicKey,String password) {
        byte[] encryptedData = new byte[0];
        try {
            PublicKey publicKeyParam = loadPublicKey(publicKey);
            // 加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKeyParam);
            encryptedData = cipher.doFinal(password.getBytes());
        } catch (Exception e) {
            logger.error("RSA加密密码异常 {}",e.getMessage());
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * 字符串公钥转换为PublicKey对象
     * @param publicKeyString
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey loadPublicKey(String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 将Base64编码的公钥字符串解码为字节数组
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        // 构建X509EncodedKeySpec对象
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        // 使用RSA算法构建KeyFactory对象
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // 生成公钥对象
        return keyFactory.generatePublic(spec);
    }

}