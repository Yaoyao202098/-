package com.lw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lw
 * @data 2023/10/31
 * @周二
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable{
    private String userName;
    private String password;
    private transient int age;
}
