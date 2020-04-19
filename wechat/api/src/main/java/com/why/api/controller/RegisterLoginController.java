package com.why.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.why.pojo.Users;
import com.why.service.UserService;
import com.why.service.impl.UserServiceImpl;
import com.why.utils.JSONResult;
import com.why.utils.MD5Utils;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.Resources;

//tags是展示在页面上的
@RestController
@Api(value = "用户注册登录的接口", tags = {"注册登陆controller"})
public class RegisterLoginController {

    @Autowired
    private UserService userService;

    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.appsecret}")
    private String appsecret;

    @PostMapping("/register")
    //  value和notes是展示在页面上的
    @ApiOperation(value = "注册", notes = "用户注册接口")
    public JSONResult userRegister (@RequestBody Users user) throws Exception {
        // 1.判断用户名密码不为空
        // 2.用户名是否存在re
        // 3.保存用户
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return JSONResult.errorMsg("用户名密码不能为空");
        } else if (userService.queryUsernameIsExist(user.getUsername())) {
//            return JSONResult.ok(user);
            return JSONResult.errorMsg(("用户名已存在"));
        } else {
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);
            userService.saveUser(user);
            return JSONResult.ok();
        }

    }
    @PostMapping("/loginbywechat")
    //  value和notes是展示在页面上的
    @ApiOperation(value = "微信登陆", notes = "用户使用微信授权登陆接口")
    public JSONResult userRegisterByWechat (@RequestBody Users user) throws Exception {
        // 0.先用传过来的code去请求openid,再把openid作为username存入数据库,前端暂时用id字段来当作code，后面存入数据库会覆盖掉没有影响
        // 1.判断用户名密码不为空
        // 2.用户名是否存在re
        // 3.保存用户
        // 拼接url
        String code = user.getId();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid
                + "&secret=" + appsecret + "&js_code=" + code + "&grant_type=authorization_code";
        // 初始化HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建HttpGet
        HttpGet httpGet = new HttpGet(url);
        // 发起请求，获取response对象
        CloseableHttpResponse response = httpClient.execute(httpGet);
        // 关闭httpclient
        httpClient.close();
        // 获取请求状态码
        int status = response.getStatusLine().getStatusCode();
        if (status != 200) {
            return JSONResult.errorMsg("获取微信信息失败，请稍后再试");
        }
        // 获取返回数据实体对象，里面有openid和session_key,对于小程序而言openid是唯一的，session_key用来后段请求用户数据
        HttpEntity entity = response.getEntity();
        // 转为字符串
         String result = EntityUtils.toString(entity,"UTF-8");
         // 解析字符串,将json字符串转为json对象
        JSONObject jsonObject = JSONObject.parseObject(result);
        String openid = jsonObject.getString("openid");
        // session_key暂时没有使用，先注释掉
        // String session_key = jsonObject.getString("session_key")
        // 将openid 当作username
        user.setUsername(openid);
        // password用openid的md5
        user.setPassword(MD5Utils.getMD5Str(user.getUsername()));
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return JSONResult.errorMsg("用户名密码不能为空");
        } else if (userService.queryUsernameIsExist(user.getUsername())) {
            // 判断用户是否在存在并返回用户
            Users userResult = userService.queryUserForLogin(openid, MD5Utils.getMD5Str(openid));
            userResult.setUsername("");
            userResult.setPassword("");
            return JSONResult.ok(userResult);
        } else {
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);
            userService.saveUser(user);
            return JSONResult.ok(user);
        }
    }
    @PostMapping("/login")
    @ApiOperation(value = "登陆", notes = "用户名密码登录")
    public JSONResult userLogin (@RequestBody Users user) throws Exception {
        String username = user.getUsername();
        String password = user.getPassword();
        if (username.isEmpty() || password.isEmpty()) {
            return JSONResult.errorMsg("用户名和密码不能为空");
        }
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (userResult == null) {
            return JSONResult.errorMsg("用户名或密码错误");
        }
        userResult.setPassword("");
        return JSONResult.ok(userResult);
    }
}
