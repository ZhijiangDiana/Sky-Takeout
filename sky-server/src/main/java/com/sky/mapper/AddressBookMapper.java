package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    AddressBook selectOne(AddressBook addressBook);
    List<AddressBook> select(AddressBook addressBook);
    void insert(AddressBook addressBook);
    void update(AddressBook addressBook);
    void delete(AddressBook addressBook);


}
