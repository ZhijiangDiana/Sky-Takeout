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
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordEditFailedException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

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
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("password md5: {}", password);
        if (!password.equals(employee.getPassword())) {
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

    @Override
    public void add(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置其他缺省值
        employee.setStatus(StatusConstant.ENABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        // 通过jwt反推出登录人
//        employee.setCreateUser(BaseContext.getCurrentId());
//        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

    @Override
    public PageResult pageSelect(EmployeePageQueryDTO employeePageQueryDTO) {
        // 分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageSelect(employeePageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Employee emp = Employee.builder()
                .id(id)
                .status(status)
                .build();

        employeeMapper.update(emp);
    }

    @Override
    public Employee idSelect(Integer id) {
        return employeeMapper.idSelect(id);
    }

    @Override
    public void updateInfo(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }

    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        Employee employee = employeeMapper.idSelect(BaseContext.getCurrentId().intValue());
        if (!employee.getPassword().equals(DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes())))
            throw new PasswordEditFailedException(MessageConstant.PASSWORD_EDIT_FAILED);

        Employee update = Employee.builder()
                .id(BaseContext.getCurrentId())
                .password(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()))
                .build();
        employeeMapper.update(update);
    }

}
