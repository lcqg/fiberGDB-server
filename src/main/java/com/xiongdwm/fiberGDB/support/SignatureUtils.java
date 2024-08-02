package com.xiongdwm.fiberGDB.support;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.sql.SQLOutput;
import java.util.Base64;

@Component
public class SignatureUtils {
    @Resource
    private RSAUtils rsaUtils;

    public String sign(String data) throws Exception {
        PrivateKey privateKey = rsaUtils.loadPrivateKeyObject();
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(data.getBytes());
        byte[] signature = privateSignature.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    public boolean verifySignature(String data, String signature) throws Exception {
        PublicKey publicKey = rsaUtils.loadPublicKeyObject();
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(data.getBytes());
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return publicSignature.verify(signatureBytes);
    }

    public static void main(String[] args) throws Exception {
        SignatureUtils signatureUtils=new SignatureUtils();
        String s="cdc-xbfb-gdb";
        String es=signatureUtils.sign(s);
        System.out.println(es);
        System.out.println(signatureUtils.verifySignature(s,es));
        RSAUtils rsaUtils=new RSAUtils();
        String a=rsaUtils.encrypt(s);
        System.out.println(a);
        System.out.println(rsaUtils.decrypt(a));
    }
}
