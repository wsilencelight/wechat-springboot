package com.why.service.impl;

import com.why.mapper.UsersMapper;
import com.why.pojo.Users;
import com.why.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersMapper userMapper;
    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Users user = new Users();
        user.setUsername(username);
        Users result = userMapper.selectOne(user);
        return result != null;
    }

    // 事务
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
        String id = sid.nextShort();
        user.setId(id);
        userMapper.insert(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin (String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria =  userExample.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);
        Users result = userMapper.selectOneByExample(userExample);
        return result;
    }
}
