package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类, 用于公共字段填充
 */
@Aspect
@Slf4j
@Component
public class PublicFieldAspect {

   @Pointcut( "execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
   public void autoFillPointcut() {}

   @Before("autoFillPointcut()")
   public void publicFieldAutoFill(JoinPoint joinPoint) {
      log.info("公共字段自动填充~");
      // 获取方法签名
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      Method method = signature.getMethod();
      // 获取数据库操作类型
      OperationType operationType = method.getAnnotation(AutoFill.class).value();
      // 获取mapper方法形参
      Object[] args = joinPoint.getArgs();
      Object property = args[0];
      // 获取当前时间和当前操作人id
      LocalDateTime now = LocalDateTime.now();
      long currentId = BaseContext.getCurrentId();
      if (operationType == OperationType.INSERT) {
          try {
              Method setCreateTime = property.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
              Method setCreateUser = property.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
              Method setUpdateTime = property.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
              Method setUpdateUser = property.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
              setCreateTime.invoke(property, now);
              setCreateUser.invoke(property, currentId);
              setUpdateTime.invoke(property, now);
              setUpdateUser.invoke(property, currentId);
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
      } else if (operationType == OperationType.UPDATE) {
          try {
              Method setUpdateTime = property.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
              Method setUpdateUser = property.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
              setUpdateTime.invoke(property, now);
              setUpdateUser.invoke(property, currentId);
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
      }
   }
}
