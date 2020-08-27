package com.esiran.greenadmin.common.util;

import java.util.List;

public class ListUtil {
    public static String list2str(List<String> data){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<data.size(); i++){
            sb.append(data.get(i));
            sb.append(i<data.size()-1?",":"");
        }
        return sb.toString();
    }
    public static <T> int findIndex(List<T> data, T s){
        for (int i=0; i<data.size(); i++){
            if(!data.get(i).equals(s))
                continue;
            return i;
        }
        return -1;
    }
}
