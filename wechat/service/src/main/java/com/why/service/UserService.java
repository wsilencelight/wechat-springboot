package com.why.service;

import com.why.pojo.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserService {
    /**
     * 判断用户名是否已经存在
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist (String username);

    /**
     * 保存用户信息
     * @param user
     */
    public void saveUser (Users user);

    /**
     * @登陆用 判断用户名密码是否匹配
     * @param username
     * @param password
     * @return
     */
    public Users queryUserForLogin (String username, String password);

}
