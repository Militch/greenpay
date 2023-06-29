package com.esiran.greenpay.common.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bouncycastle.util.Strings;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapUtil {
    private static final Gson g = new Gson();
    public static <T> Map<String,String> entity2stringMap(T t){
        String json = g.toJson(t);
        return jsonString2stringMap(json);
    }

    public static <T> Map<String,String> httpQueryString2map(String s){
        if (s == null || s.length() == 0 ) return null;
        String[] ss = s.split("&");
        Map<String,String> map = new HashMap<>();
        for (String item : ss){
            Pattern pattern = Pattern.compile("^(.+?)=(.+?)$");
            Matcher matcher = pattern.matcher(item);
            if (matcher.matches()){
                String key = matcher.group(1);
                String val = matcher.group(2);
                map.put(key,val);
            }
        }
        return map;
    }
    public static <T> String map2httpQuery(Map<String,String> map){
        List<String> keys = new ArrayList<>(map.keySet());
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<keys.size(); i++){
            String key = keys.get(i);
            sb.append(key).append("=")
                    .append(map.get(key))
                    .append(i<keys.size()-1?"&":"");
        }
        return sb.toString();
    }
    public static <T> Map<String,Object> entity2objMap(T t){
        String json = g.toJson(t);
        return jsonString2objMap(json);
    }
    public static Map<String,String> jsonString2stringMap(String json){
        if (StringUtils.isEmpty(json)) return null;
        return g.fromJson(json,new TypeToken<Map<String,String>>(){}.getType());
    }
    public static Map<String,Object> jsonString2objMap(String json){
        if (StringUtils.isEmpty(json)) return null;
        return g.fromJson(json,new TypeToken<Map<String,Object>>(){}.getType());
    }
    public static String sortAndSerialize(Map<String,String> params, String[] excludeKeys){
        Set<String> keys = params.keySet();
        List<String> keyList = new ArrayList<>(keys);
        Collections.sort(keyList);
        StringBuilder sb = new StringBuilder();
        List<String> excludeKeyList = excludeKeys==null?new ArrayList<>()
                :Arrays.asList(excludeKeys);
        for (int i=0; i<keyList.size(); i++){
            String key = keyList.get(i);
            if (excludeKeyList.contains(key))
                continue;
            sb.append(key).append("=")
                    .append(params.get(key))
                    .append(i<keyList.size()-1?"&":"");
        }
        return sb.toString();
    }

    public static String sortAndSerialize(Map<String,String> params){
        return sortAndSerialize(params,null);
    }
}
