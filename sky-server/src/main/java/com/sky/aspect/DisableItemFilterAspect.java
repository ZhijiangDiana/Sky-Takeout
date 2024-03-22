package com.sky.aspect;

import com.sky.constant.StatusConstant;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class DisableItemFilterAspect {
    @Pointcut("execution(* com.sky.controller.*.*(..)) && @annotation(com.sky.annotation.FilterDisabled)")
    public void disableItemFilterPointCut() {}

    @Around("@annotation(com.sky.annotation.FilterDisabled)")
    public Object disableItemFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Object res = joinPoint.proceed();

//        log.info("正在修改返回结果");

        if (!(res instanceof Result))
            return res;
        Object data = ((Result<?>) res).getData();
        if (!(data instanceof List))
            return res;
        List<?> resList = (List<?>) data;
        if (resList.isEmpty())
            return res;

        Class<?> clazz = resList.get(0).getClass();
        Method getStatus = clazz.getMethod("getStatus");

        resList = resList.stream().filter(x -> {
            try {
                return getStatus.invoke(x) == StatusConstant.ENABLE;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        return Result.success(resList);
    }
}
