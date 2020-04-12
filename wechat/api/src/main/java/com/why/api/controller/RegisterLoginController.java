package com.why.api.controller;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.why.pojo.Users;
import com.why.service.UserService;
import com.why.service.impl.UserServiceImpl;
import com.why.utils.JSONResult;
import com.why.utils.MD5Utils;
import io.netty.util.internal.StringUtil;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

@RestController
public class RegisterLoginController {
    @PostMapping("/register")
    public JSONResult userRegister (Users user) throws Exception {
        // 1.判断用户名密码不为空
        // 2.用户名是否存在
        // 3.保存用户
        UserServiceImpl userService = new UserServiceImpl();
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return JSONResult.errorMsg("用户名密码不能为空");
        } else if (userService.queryUsernameIsExist(user.getUsername())) {
//            return JSONResult.ok(user);
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);
            userService.saveUser(user);
            return JSONResult.ok();
        } else {
            return JSONResult.errorMsg(("用户名已存在"));
        }

    }
}
