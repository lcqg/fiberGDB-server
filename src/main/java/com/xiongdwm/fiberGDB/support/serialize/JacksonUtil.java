package com.xiongdwm.fiberGDB.support.serialize;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class JacksonUtil {
    static ObjectMapper objectMapper=new ObjectMapper();

    public static String toString(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            System.out.println(e.getLocalizedMessage());
        }
        return "";
    }

    public static JsonNode jsonStringToJsonNode(String s) throws IOException {
        return objectMapper.readTree(s);
    }

    public static JsonNode inputStreamToJsonNode(InputStream inputStream) throws IOException {
        return objectMapper.readTree(inputStream);
    }

    public static<T> T parseObject(String str,Class<T> clazz){
        try{
            return objectMapper.readValue(str,clazz);
        }catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }

    public static<T> T parseObject(JsonNode jsonNode,Class<T> clazz){
        try{
            return objectMapper.treeToValue(jsonNode,clazz);
        }catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }

    public static<T> List<T> parseArray(String str,Class<T> clazz){
        try{
            return objectMapper.readValue(str,objectMapper.getTypeFactory().constructCollectionType(List.class,clazz));
        }catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }

    public static List<Object>parseArray(String str){
        try{
            return objectMapper.readValue(str, new TypeReference<>() {});
        }catch (IOException e){
            System.out.println(e.getLocalizedMessage());
        }
        return null;
    }

    public static JsonNode convertToJsonNode(Object obj){
        String jsonString=toString(obj);
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(jsonString);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return jsonNode;
    }

    public static<T> T mapToEntity(Map<String,?> src, Class<T> clazz) throws IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException, NoSuchMethodException {
        T entity=clazz.getDeclaredConstructor().newInstance();
        PropertyDescriptor pd;
        for(Field field: clazz.getDeclaredFields()){
            field.setAccessible(true);
            JsonProperty annotation=field.getAnnotation(JsonProperty.class);
            String alias=annotation.value();
            Object value=src.get(alias);
            pd=new PropertyDescriptor(field.getName(),clazz);
            Method method=pd.getWriteMethod();
            method.invoke(entity,value);
        }
        return entity;
    }

    public static List<ObjectNode> mapToJsonArray(Map<String,String>map){
        List<ObjectNode>nodes = new ArrayList<>();
        for(Map.Entry<String,String> entry:map.entrySet()){
            ObjectNode obj= objectMapper.createObjectNode();
            obj.put("key",entry.getKey());
            obj.put("value",entry.getValue());
            nodes.add(obj);
        }
        return nodes;
    }

    public static ObjectNode mapToNode(Map<String,Object>map){
        return objectMapper.valueToTree(map);
    }

    public static List<ObjectNode>listToJsonArray(List<String> list,boolean needDefault){
        if(list.isEmpty())return Collections.emptyList();
        List<ObjectNode>nodes=new ArrayList<>();
        for(String s:list){
            ObjectNode obj=objectMapper.createObjectNode();
            obj.put("name",s);
            nodes.add(obj);
        }
        if(needDefault){
            ObjectNode objd=objectMapper.createObjectNode();
            objd.put("name","无");
            nodes.add(objd);
        }
        return nodes;
    }

    public static Map<String, Object>  convertEntityToMap(Object entity) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if(field.isAnnotationPresent(JsonFilter.class))continue;
            String fieldName = field.getName();
            Object fieldValue = field.get(entity);
            if (fieldValue == null)continue;
            map.put(fieldName, fieldValue);
        }
        return map;
    }

    public static JsonNode getJasonNode(String name,JsonNode jsonNode){
        return jsonNode.get(name);
    }


}
