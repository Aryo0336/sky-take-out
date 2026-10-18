package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 根据openId查询用户
     * @param openId
     * @return
     */
    @Select("select * from sky_take_out.user where openid = #{openId}")
    User getByOpenId(String openId);

    /**
     * 用户注册
     * @param user
     */
    @Insert("insert into sky_take_out.user(openid, name, create_time) values (#{openid}, #{name} ,#{createTime})")
    void userAdd(User user);
}
