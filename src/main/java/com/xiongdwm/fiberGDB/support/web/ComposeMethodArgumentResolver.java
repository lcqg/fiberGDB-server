package com.xiongdwm.fiberGDB.support.web;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

public class ComposeMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private final ExtendedRequestParamResolver extendedRequestParamResolver=new ExtendedRequestParamResolver(true);

    private final ServletModelAttributeMethodProcessor servletModelAttributeMethodProcessor = new ServletModelAttributeMethodProcessor(true);

    public boolean supportsParameter(MethodParameter parameter) {
        return servletModelAttributeMethodProcessor.supportsParameter(parameter) || extendedRequestParamResolver.supportsParameter(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object arg = extendedRequestParamResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        if (arg == null && servletModelAttributeMethodProcessor.supportsParameter(parameter)) {
            arg = servletModelAttributeMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }
        return arg;
    }
}