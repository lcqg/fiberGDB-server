package com.xiongdwm.fiberGDB.support.binlogSync;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtils {
    public static String snakeToUpperCamelCase(String input) {
        String[] array=input.split("_");
        if(array.length==1)return input;
        return Arrays.stream(array)
                .map(word -> word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining());
    }

    public static String snakeToLowerCamelCase(String input) {
        String[] parts = input.split("_");

        // 将第一个单词保持小写
        String firstWord = parts[0].toLowerCase();

        // 处理其余单词，首字母大写，其余字母小写
        String camelCaseRest = Arrays.stream(parts, 1, parts.length)
                .map(word -> word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining());

        // 拼接结果
        return firstWord + camelCaseRest;
    }

}
