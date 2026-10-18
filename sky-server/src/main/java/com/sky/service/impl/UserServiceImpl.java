package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String WX_AUTH_PATH = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 小程序微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 获取微信登录凭证
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_AUTH_PATH, map);
        // 查询用户
        JSONObject jsonObject = JSONObject.parseObject(json);
        String openId = jsonObject.getString("openid");
        User user = getByOpenId(openId);
        // 判断用户是否存在, 若用户不存在则自动注册
        if (user == null) {
            User newUser = User.builder()
                    .openid(openId)
                    .name("用户" + openId)  // 初始用户名-> 用户+openid
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.userAdd(newUser);
            return getByOpenId(openId);
        }
        return user;
    }

    /**
     * 根据openID查询用户
     * @param openId
     * @return
     */
    @Override
    public User getByOpenId(String openId) {
        return userMapper.getByOpenId(openId);
    }
}
