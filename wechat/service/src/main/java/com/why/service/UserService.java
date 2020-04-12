package com.why.service;

import com.why.pojo.Users;

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

}
