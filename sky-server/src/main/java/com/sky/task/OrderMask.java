package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class OrderMask {

    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    public void pressTimeOrder(){
        log.info("定时处理超时订单:{}", LocalDateTime.now());

    }
}
