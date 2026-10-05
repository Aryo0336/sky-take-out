package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from sky_take_out.employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 新增员工
     * @param employee
     */
    @Insert("insert into sky_take_out.employee" +
            "(name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user)" +
            "values" +
            "(#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void save(Employee employee);

    /**
     * 员工分页查询
     * @param empPageQueryVO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO empPageQueryDTO);

    /**
     * 根据员工id查询
     * @param id
     * @return
     */
    @Select("select id,name,username,id_number,sex,phone,status,create_time,update_time,create_user, update_user" +
            " from sky_take_out.employee where id = #{id}")
    Employee empQueryById(long id);

    /**
     * 更新员工信息
     * @param employeeDTO
     */
    void update(Employee employeeDTO);
}
