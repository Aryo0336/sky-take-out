package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 使用Argon2PasswordEncoder进行加密解密
        if (!passwordEncoder.matches(password, employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        // 设置初始密码
        String encode = passwordEncoder.encode(PasswordConstant.DEFAULT_PASSWORD);
        Employee employee = Employee.builder()
                .idNumber(employeeDTO.getIdNumber())
                .username(employeeDTO.getUsername())
                .name(employeeDTO.getName())
                .phone(employeeDTO.getPhone())
                .sex(employeeDTO.getSex())
                .password(encode)
                .status(1) // 设置初始状态为启用
                .build();
        employeeMapper.save(employee);
        BaseContext.removeCurrentId();
    }

    /**
     * 员工分页查询
     * @param empPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(EmployeePageQueryDTO empPageQueryDTO) {
        // 开始分页查询
        PageHelper.startPage(empPageQueryDTO.getPage(), empPageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(empPageQueryDTO);
        List<Employee> records = page.getResult();
        return new PageResult(page.getTotal(), records);
    }

    /**
     * 根据员工id查询
     * @param id
     * @return
     */
    @Override
    public Employee empQueryById(long id) {
        return employeeMapper.empQueryById(id);
    }

    /**
     * 更新员工信息
     * @param employeeDTO
     */
    @Override
    public void update( EmployeeDTO employeeDTO) {
        Employee employee = Employee.builder()
                        .id(employeeDTO.getId())
                        .name(employeeDTO.getName())
                        .username(employeeDTO.getUsername())
                        .phone(employeeDTO.getPhone())
                        .sex(employeeDTO.getSex())
                        .idNumber(employeeDTO.getIdNumber())
                        .build();
        employeeMapper.update(employee);
    }

    /**
     * 切换员工账号状态
     * @param id
     * @param status
     */
    @Override
    public void switchEmpStatus(long id, Integer status) {
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();
        employeeMapper.update(employee);
    }
}
