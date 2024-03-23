package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void addNewAddress(AddressBook addressBook);

    List<AddressBook> listAllAddress();

    AddressBook getDefaultAddress();

    void modifyAddress(AddressBook addressBook);

    void removeAddress(Long id);

    AddressBook getAddressById(Long id);

    void modifyDefaultAddress(Long id);
}
