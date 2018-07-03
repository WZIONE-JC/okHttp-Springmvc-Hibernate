package com.baidu.zhaocc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonIgnore
    private int id;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;

}
