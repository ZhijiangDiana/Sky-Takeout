package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    Employee getByUsername(String username);

    @AutoFill(value = OperationType.INSERT)
    void insert(Employee employee);

    Page<Employee> pageSelect(EmployeePageQueryDTO employeePageQueryDTO);

    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);

    Employee idSelect(@Param("id") Integer id);
}
