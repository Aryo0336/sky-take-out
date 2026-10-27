package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sales implements Serializable {

    private static final long serialVersionUID = 1L;

    // 菜品或套餐名
    private String name;
    // 销量
    private Long total;
}
