package com.why.service.controller;

import com.why.service.entity.user;
import org.apache.catalina.mbeans.MBeanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
//@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class helloworld {
    @Autowired
    private user u1;

    @RequestMapping("/user")
    public user getUserInfo() {
        user u = new user();
        u.setDate(new Date());
        u.setName("why");
        u.setPassword("123");
        return u;
    }

    @RequestMapping("/getuser")
    public  user getUser () {
        user u = new user();
        BeanUtils.copyProperties(u1, u);
        return u;
    }


}