package com.esiran.greenadmin.common.wrapper;

import com.esiran.greenadmin.common.util.IOUtil;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonRequestWrapper extends HttpServletRequestWrapper {
    private byte[] bytes;
    private byte[] target;
    private ServletInputStream inputStream;
    private static class RequestCachingInputStream extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        public RequestCachingInputStream(byte[] bytes) {
            inputStream = new ByteArrayInputStream(bytes);
        }
        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readlistener) {
        }

    }
    public JsonRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        bytes = IOUtil.readInputByte(request.getInputStream());
        target = bytes;
        inputStream = new RequestCachingInputStream(target);
    }

    public byte[] getBytes() {
        return bytes;
    }
    public String getBody(){
        return new String(bytes);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(target);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return inputStream;
    }
}
