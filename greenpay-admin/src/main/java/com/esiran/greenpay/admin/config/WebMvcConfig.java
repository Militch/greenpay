package com.esiran.greenpay.admin.config;

import com.esiran.greenpay.admin.runner.AgentPayQueryTaskRunner;
import com.esiran.greenpay.admin.runner.OrderACPayTaskRunner;
import com.esiran.greenpay.admin.runner.OrderNotifyTaskRunner;
import com.esiran.greenpay.common.util.IdWorker;
import com.esiran.greenpay.message.delayqueue.DelayQueueTaskRegister;
import com.esiran.greenpay.admin.runner.OrderExpireTaskRunner;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableSwagger2
public class WebMvcConfig implements WebMvcConfigurer {
    private final BaseInterceptor baseInterceptor;
    private final OrderNotifyTaskRunner orderNotifyTaskRunner;
    private final OrderExpireTaskRunner orderExpireTaskRunner;
    private final OrderACPayTaskRunner orderACPayTaskRunner;
    private final AgentPayQueryTaskRunner agentPayQueryTaskRunner;
    public WebMvcConfig(BaseInterceptor baseInterceptor,
                        OrderNotifyTaskRunner orderNotifyTaskRunner, OrderExpireTaskRunner orderExpireTaskRunner, OrderACPayTaskRunner orderACPayTaskRunner, AgentPayQueryTaskRunner agentPayQueryTaskRunner) {
        this.baseInterceptor = baseInterceptor;
        this.orderNotifyTaskRunner = orderNotifyTaskRunner;
        this.orderExpireTaskRunner = orderExpireTaskRunner;
        this.orderACPayTaskRunner = orderACPayTaskRunner;
        this.agentPayQueryTaskRunner = agentPayQueryTaskRunner;
    }

    @Bean
    public IdWorker idWorker(){
        return new IdWorker(1,1,1);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .maxAge(3600);
    }
    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule).registerModule(new ParameterNamesModule());
        return objectMapper;
    }

    @Bean
    public DelayQueueTaskRegister delayQueueTaskRegister(){
        DelayQueueTaskRegister delayQueueTaskRegister = new DelayQueueTaskRegister();
        delayQueueTaskRegister.register("order:expire",orderExpireTaskRunner);
        delayQueueTaskRegister.register("order:acpay",orderACPayTaskRunner);
        delayQueueTaskRegister.register("order:notify",orderNotifyTaskRunner);
        delayQueueTaskRegister.register("agentpay:query",agentPayQueryTaskRunner);
        return delayQueueTaskRegister;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor).addPathPatterns("/**");
    }
}
