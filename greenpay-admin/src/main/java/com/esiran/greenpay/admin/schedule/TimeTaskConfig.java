package com.esiran.greenpay.admin.schedule;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
public class TimeTaskConfig implements SchedulingConfigurer {

    /**
     * 配置线程池大小
     * @param scheduledTaskRegistrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        //scheduledTaskRegistrar.setScheduler(Executors.newScheduledThreadPool(10));
    }

//    @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "0/5 * * * * ?")
    public void test1(){

    }

}