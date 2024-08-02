package com.xiongdwm.fiberGDB.support.web;


import com.xiongdwm.fiberGDB.support.SignatureUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import java.text.SimpleDateFormat;

public class WebInterceptor implements HandlerInterceptor {
    @Value("${spring.profiles.active}")
    private String active;
    @Resource
    private SignatureUtils signatureUtils;
    private static String signatureCheck="cdc-xiong-fbrdb";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if("dev".equals(active)){
            return true;
        }

        String token = request.getHeader("token");
        String signature= request.getHeader("signature");
        if(null==token||null==signature) {
            response.setStatus(401);
            return false;
        }
        long now = System.currentTimeMillis();
        String tokenTimeString=token.split("_")[1];
        long tokenTime = Long.parseLong(tokenTimeString);
        if(now-tokenTime>5*60*1000) { // 5min
            response.setStatus(401);
            return false;
        }
        String tokenUser = token.split("_")[0];
        try {
            boolean signatureString= signatureUtils.verifySignature(signatureCheck,signature)||
                    signatureUtils.verifySignature("cdc-xiong-fbgdb",signature);
            if(!signatureString) {
                response.setStatus(401);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        System.out.println(simpleDateFormat.format(System.currentTimeMillis()));
        Long now= Long.parseLong(simpleDateFormat.format(System.currentTimeMillis()));
        Long tokenTime= Long.parseLong("202311271100");
        Long s=Long.parseLong("202311271059");
        System.out.println(tokenTime-s);
        var signature="cdc-xiong-fbgdb";
        SignatureUtils su=new SignatureUtils();
        su.verifySignature(signature,"cdc-xiong-fbgdb");

    }
}
