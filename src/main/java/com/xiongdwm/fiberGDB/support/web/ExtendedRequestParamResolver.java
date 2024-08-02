package com.xiongdwm.fiberGDB.support.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.xiongdwm.fiberGDB.support.serialize.JacksonUtil;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;


import java.io.IOException;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ExtendedRequestParamResolver extends RequestParamMethodArgumentResolver {
    private final boolean useDefaultResolution = true;
    private static final String JSON_BODY_ATTRIBUTE = "JSON_REQUEST_BODY";

    public ExtendedRequestParamResolver(boolean useDefaultResolution) {
        super(useDefaultResolution);
    }

    public ExtendedRequestParamResolver(ConfigurableBeanFactory beanFactory, boolean useDefaultResolution) {
        super(beanFactory, useDefaultResolution);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return super.supportsParameter(parameter)||!parameter.hasParameterAnnotations();
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Object arg = super.resolveName(name, parameter, request);
        if (arg == null) {
            JsonNode requestBody = getRequestBody(request);
            if (requestBody == null) return null;
            JsonNode argValue = JacksonUtil.getJasonNode(name,requestBody);
            Class<?> parameterType = parameter.getParameterType();
            if (parameterType.isPrimitive()) {
                arg = parsePrimitive(parameterType.getName(), argValue);
            } else if (isBasicDataTypes(parameterType)) {
                arg = parseBasicTypeWrapper(parameterType, argValue);
            } else if (parameterType.isEnum()) {
                arg = getEnumFromString((Class) parameterType, Objects.requireNonNullElse(argValue, requestBody).toString());
            } else if (parameterType == List.class) {
                if (argValue != null) {
                    Class<?> actualType = getActualType(parameter.getGenericParameterType());
                    if (actualType == null) {
                        arg = JacksonUtil.parseArray(argValue.toString());
                    } else {
                        arg = JacksonUtil.parseArray(argValue.toString(), actualType);
                    }
                }
            } else if (parameterType.isArray()) {
                if (argValue != null) {
                    Class<?> actualType = parameterType.getComponentType();
                    if (actualType == null) {
                        arg = JacksonUtil.parseArray(argValue.toString());
                    } else {
                        arg = JacksonUtil.parseObject(argValue.toString(), actualType);
                    }
                }
            } else if (argValue != null) {
                arg = JacksonUtil.parseObject(argValue.toString(), parameterType);
            } else {
                arg = JacksonUtil.parseObject(requestBody, parameterType);
            }
        }
        return arg;
    }

    // 基本类型解析
    private Object parsePrimitive(String parameterTypeName, Object value) {
        if (value == null)
            return null;

        final String booleanTypeName = "boolean";
        if (booleanTypeName.equals(parameterTypeName))
            return Boolean.valueOf(value.toString());

        final String intTypeName = "int";
        if (intTypeName.equals(parameterTypeName))
            return Integer.valueOf(value.toString());

        final String charTypeName = "char";
        if (charTypeName.equals(parameterTypeName))
            return value.toString().charAt(0);

        final String shortTypeName = "short";
        if (shortTypeName.equals(parameterTypeName))
            return Short.valueOf(value.toString());

        final String longTypeName = "long";
        if (longTypeName.equals(parameterTypeName))
            return Long.valueOf(value.toString());

        final String floatTypeName = "float";
        if (floatTypeName.equals(parameterTypeName))
            return Float.valueOf(value.toString());

        final String doubleTypeName = "double";
        if (doubleTypeName.equals(parameterTypeName))
            return Double.valueOf(value.toString());

        final String byteTypeName = "byte";
        if (byteTypeName.equals(parameterTypeName))
            return Byte.valueOf(value.toString());

        return null;
    }

    // 基本类型包装类型解析
    private Object parseBasicTypeWrapper(Class<?> parameterType, Object value) {
        if (value == null) {
            return null;
        }
        if (Number.class.isAssignableFrom(parameterType)) {
            Number number = (Number) value;
            if (parameterType == Integer.class) {
                return number.intValue();
            } else if (parameterType == Short.class) {
                return number.shortValue();
            } else if (parameterType == Long.class) {
                return number.longValue();
            } else if (parameterType == Float.class) {
                return number.floatValue();
            } else if (parameterType == Double.class) {
                return number.doubleValue();
            } else if (parameterType == Byte.class) {
                return number.byteValue();
            }
        } else if (parameterType == Boolean.class || parameterType == String.class) {
            return value.toString();
        } else if (parameterType == Character.class) {
            return value.toString().charAt(0);
        }
        return null;
    }

    private boolean isBasicDataTypes(Class<?> clazz) {
        Set<Class<?>> classSet = new HashSet<>();
        classSet.add(String.class);
        classSet.add(Integer.class);
        classSet.add(Long.class);
        classSet.add(Short.class);
        classSet.add(Float.class);
        classSet.add(Double.class);
        classSet.add(Boolean.class);
        classSet.add(Byte.class);
        classSet.add(Character.class);
        return classSet.contains(clazz);
    }

    public static Class<?> getActualType(Type type) {
        if (type instanceof ParameterizedType pt) {
            Type actualType = pt.getActualTypeArguments()[0];
            if (actualType instanceof TypeVariable) {
                return (Class<?>) ((TypeVariable<?>) actualType).getBounds()[0];
            } else if (actualType instanceof WildcardType wildcardType) {
                Type[] bounds = wildcardType.getLowerBounds();
                if (bounds.length == 0) {
                    bounds = wildcardType.getUpperBounds();
                }
                return (Class<?>) bounds[0];
            } else if (actualType instanceof GenericArrayType) {
                return (Class<?>) ((GenericArrayType) actualType).getGenericComponentType();
            } else if (actualType instanceof Class) {
                return (Class<?>) actualType;
            }
        }
        return null;
    }

    public <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if (c != null && string != null) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        }
        return null;
    }

    private JsonNode getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        JsonNode jsonBody = (JsonNode) webRequest.getAttribute(JSON_BODY_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        if (jsonBody == null && request != null) {
            try {
                System.out.println(read(request.getInputStream()));
                jsonBody = JacksonUtil.jsonStringToJsonNode(read(request.getInputStream()));
                webRequest.setAttribute(JSON_BODY_ATTRIBUTE, jsonBody, NativeWebRequest.SCOPE_REQUEST);
            } catch (IOException e) {
                return null;
            }
        }
        return jsonBody;
    }

    private String read(ServletInputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (!inputStream.isFinished()) {
            byte[] bytes = new byte[inputStream.available()];
            int i=inputStream.read(bytes);
            if(i>0)sb.append(new String(bytes));
        }
        return sb.toString();
    }
}
