package com.esiran.greenadmin.common.util;

import okhttp3.FormBody;

import java.util.Map;

public class OkHttpUtil {

    public static FormBody map2fromBody(Map<String,String> map){
        FormBody.Builder formbodyBuider = new FormBody.Builder();
        map.forEach(formbodyBuider::add);
        return formbodyBuider.build();
    }
}
