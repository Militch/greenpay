package com.esiran.greenpay.actuator;

import com.esiran.greenpay.actuator.entity.Flow;
import com.esiran.greenpay.actuator.entity.Task;

public class Main {
    private static class WxjsapiPlugin implements Plugin<Object> {

        @Override
        public void apply(Flow<Object> flow) {
            flow.add(new Task<Object>() {
                @Override
                public String taskName() {
                    return null;
                }

                @Override
                public String dependent() {
                    return null;
                }

                @Override
                public void action(Flow<Object> flow) throws Exception {

                }
            });
        }
    }
    public static void main(String[] args) {

    }
}
