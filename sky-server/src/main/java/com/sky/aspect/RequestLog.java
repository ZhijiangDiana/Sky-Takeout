package com.sky.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class RequestLog {
    @Around("execution(* com.sky.controller.*.*.*(..))")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String url = request.getRequestURI();
        String ip = request.getRemoteHost();
        int port = request.getRemotePort();

        // 执行被通知的方法
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long processingTime = endTime - startTime;

        log.info("{}:{} has requested {} using {}ms", ip, port, url, processingTime);

        return result;
    }
}
