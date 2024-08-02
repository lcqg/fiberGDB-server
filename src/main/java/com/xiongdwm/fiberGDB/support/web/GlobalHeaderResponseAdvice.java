package com.xiongdwm.fiberGDB.support.web;

import com.xiongdwm.fiberGDB.support.RSAUtils;
import com.xiongdwm.fiberGDB.support.SignatureUtils;
import com.xiongdwm.fiberGDB.support.View;
import jakarta.annotation.Resource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class GlobalHeaderResponseAdvice implements ResponseBodyAdvice<Object> {
    @Resource
    private SignatureUtils signatureUtils;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpHeaders headers=response.getHeaders();
        String signature= null;
        try {
            signature = signatureUtils.sign("cdc-xiong-fbgdb");
        } catch (Exception e) {
            System.out.println("encoded signature failed: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatusCode.valueOf(401));
            return View.getError(e.getLocalizedMessage());
        }
        headers.add("signature",signature);
        return body;
    }
}
