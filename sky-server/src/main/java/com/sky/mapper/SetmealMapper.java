package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SetmealMapper {
    void deleteByCatId(@Param("id") Long id);

    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    Page<SetmealVO> selectPage(SetmealPageQueryDTO setmealPageQueryDTO);

    Setmeal selectById(@Param("id") Long id);

    void deleteByIds(List<Long> ids);

    SetmealVO selectWithCatById(@Param("id") Long id);

    void update(Setmeal setmeal);

    List<Setmeal> selectByCatId(@Param("categoryId") Long categoryId);

    Integer selectCntByStatus(@Param("status") Integer status);
}
