package com.esiran.greenpay.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ResUtil {
    public static void printAlert(HttpServletResponse response, String msg){
        if (response == null)
            throw new NullPointerException("response is null");
        response.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String body = "<script>alert('" + msg + "');</script>";
            out.println(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
