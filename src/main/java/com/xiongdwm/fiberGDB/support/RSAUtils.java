package com.xiongdwm.fiberGDB.support;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RSAUtils {

   @Value("${keys.path}")
   private String outPath;
//   private String outPath="/Users/xiong/Program/dev_root/var/key";

    private static final String ENCRYPT_MODE = "RSA";
    private static final int KEY_SIZE = 1024;
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    @PostConstruct
    public void init() throws Exception {
        File dir = new File(outPath);
        if (!dir.exists()) {
            boolean idCreate=dir.mkdir();
            if (!idCreate){
                System.out.println("创建文件夹失败");
            }
        }
        KeyPair keyPair = genKeyPair();
        saveKey(keyPair);
    }

    public KeyPair genKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ENCRYPT_MODE);
        kpg.initialize(KEY_SIZE);
        System.out.println("--------生成密钥对--------");
        return kpg.genKeyPair();
    }

    public void saveKey(KeyPair keyPair) throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        FileOutputStream outPub = new FileOutputStream(outPath + "/key.pub");
        outPub.write(encoder.encode(publicKey.getEncoded()));
        outPub.close();
        FileOutputStream outPvt = new FileOutputStream(outPath + "/key");
        outPvt.write(encoder.encode(privateKey.getEncoded()));
        outPvt.close();
        System.out.println("--------写入密钥--------");
    }

    public String loadPublicKey() throws Exception {
        FileInputStream inStream = new FileInputStream(outPath +File.separator+"key.pub");
        byte[] bytes = inStream.readAllBytes();
        return new String(bytes);
    }

    public String loadPrivateKey() throws Exception {
        FileInputStream inStream = new FileInputStream(outPath +File.separator+"key");
        byte[] bytes = inStream.readAllBytes();
        return new String(bytes);
    }

    public PublicKey loadPublicKeyObject() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(loadPublicKey());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPT_MODE);
        return keyFactory.generatePublic(keySpec);
    }

    public PrivateKey loadPrivateKeyObject() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(loadPrivateKey());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPT_MODE);
        return keyFactory.generatePrivate(keySpec);
    }

    public String decrypt(String info) throws Exception {
        PrivateKey privateKey=loadPrivateKeyObject();
        byte[] base64Bytes=Base64.getDecoder().decode(info);
        Cipher cipher=Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        byte[] dataBytes= cipher.doFinal(base64Bytes);
        return new String(dataBytes,StandardCharsets.UTF_8);
    }

    //加密
    public String encrypt(String data) throws Exception {
        PublicKey publicKey=loadPublicKeyObject();
        Cipher cipher=Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        byte[] bytes=data.getBytes(StandardCharsets.UTF_8);
        byte[] encodedBytes=cipher.doFinal(bytes);
        return Base64.getEncoder().encodeToString(encodedBytes);
    }

}
