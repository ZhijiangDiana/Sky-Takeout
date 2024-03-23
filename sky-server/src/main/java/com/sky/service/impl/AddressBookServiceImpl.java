package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.exception.AddressBookBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    AddressBookMapper addressBookMapper;

    @Override
    public void addNewAddress(AddressBook addressBook) {
        log.info(addressBook.toString());
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    @Override
    public List<AddressBook> listAllAddress() {
        Long userId = BaseContext.getCurrentId();
        AddressBook query = AddressBook.builder().userId(userId).build();
        return addressBookMapper.select(query);
    }

    @Override
    public AddressBook getDefaultAddress() {
        AddressBook query = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .isDefault(1)
                .build();
        return addressBookMapper.selectOne(query);
    }

    @Override
    public void modifyAddress(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public void removeAddress(Long id) {
        AddressBook query = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .id(id)
                .build();
        addressBookMapper.delete(query);
    }

    @Override
    public AddressBook getAddressById(Long id) {
        AddressBook query = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .id(id)
                .build();
        return addressBookMapper.selectOne(query);
    }

    @Override
    public void modifyDefaultAddress(Long id) {
        AddressBook newDefaultAddr = AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .isDefault(1)
                .build();
        // 默认地址已存在，将其改为非默认地址
        AddressBook previousDefaultAddr = addressBookMapper.selectOne(newDefaultAddr);
        if (previousDefaultAddr != null) {
            previousDefaultAddr.setIsDefault(0);
            addressBookMapper.update(previousDefaultAddr);
        }
        // 设置为默认地址
        newDefaultAddr.setId(id);
        addressBookMapper.update(newDefaultAddr);
    }
}
