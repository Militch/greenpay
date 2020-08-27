package com.esiran.greenadmin.common.util;

import java.io.*;

public class IOUtil {
    public static byte[] readInputByte(InputStream inputStream){
        if (inputStream == null) return null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int nRead;
            while ((nRead = inputStream.read(buffer,0,buffer.length)) != -1){
                baos.write(buffer,0,nRead);
            }
            return baos.toByteArray();
        }catch (IOException e){
            try {
                inputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String readInputString(InputStream inputStream) {
        if (inputStream == null) return null;
        try {
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[1024];
            int nRead;
            while ((nRead = inputStream.read(buffer,0,buffer.length)) != -1){
                String str = new String(buffer,0,nRead);
                sb.append(str);
            }
            return sb.toString();
        }catch (IOException e){
            try {
                inputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static void writeStringByOutput(OutputStream outputStream,String output){

    }
}
