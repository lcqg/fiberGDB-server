package com.xiongdwm.fiberGDB.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.xiongdwm.fiberGDB.support.serialize.JacksonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class View {
    public static JsonNode SUCCESS;
    static {
        try {
            SUCCESS = JacksonUtil.jsonStringToJsonNode("{\"success\":true}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonNode FAILURE;
    static{
        try {
            FAILURE=JacksonUtil.jsonStringToJsonNode("{\"success\":false}");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static JsonNode getSuccess(Object result){
        Map<String,Object> map=new HashMap<>(2);
        map.put("success",true);
        map.put("result",result);
        return JacksonUtil.mapToNode(map);
    }

    public static JsonNode getError(String msg){
        Map<String,Object>map=new HashMap<>(2);
        map.put("success",false);
        map.put("message",msg);
        return JacksonUtil.mapToNode(map);
    }

    public static void main(String[] args) {
        JsonNode success = View.SUCCESS;
        System.out.println(success);
        JsonNode fail=View.FAILURE;
        System.out.println(fail);
        JsonNode s=View.getError("sadf");
        JsonNode a=View.getSuccess(7889L);
        System.out.println(s);
        System.out.println(a);

    }


}
