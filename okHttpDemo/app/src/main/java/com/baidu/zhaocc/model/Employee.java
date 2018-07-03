package com.baidu.zhaocc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Created by zhaochaochao@baidu.com on 2018/6/28.
 */

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
@NoArgsConstructor
public class Employee {
    private String firstName;
    private String lastName;
}
