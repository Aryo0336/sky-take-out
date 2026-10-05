package com.sky.context;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        log.info("设置当前用户id为{}", id);
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        Long id = threadLocal.get();
        System.out.println(id);
        return id;
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
