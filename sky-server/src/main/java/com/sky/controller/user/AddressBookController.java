package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(value = "用户收货地址相关接口")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("")
    @ApiOperation("新增地址")
    public Result<String> addNewAddress(@RequestBody AddressBook addressBook) {
        addressBookService.addNewAddress(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询登录用户所有地址")
    public Result<List<AddressBook>> listAllAddress() {
        List<AddressBook> res = addressBookService.listAllAddress();
        return Result.success(res);
    }

    @GetMapping("/default")
    @ApiOperation("查询登录用户默认地址")
    public Result<AddressBook> getDefaultAddress() {
        AddressBook res = addressBookService.getDefaultAddress();
        return Result.success(res);
    }

    @PutMapping("")
    @ApiOperation("修改地址")
    public Result<String> modifyAddress(@RequestBody AddressBook addressBook) {
        addressBookService.modifyAddress(addressBook);
        return Result.success();
    }

    @DeleteMapping("")
    public Result<String> removeAddress(Long id) {
        addressBookService.removeAddress(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getAddressById(@PathVariable Long id) {
        AddressBook res = addressBookService.getAddressById(id);
        return Result.success(res);
    }

    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result<String> modifyDefaultAddress(@RequestBody AddressBook addressBook) {
        addressBookService.modifyDefaultAddress(addressBook.getId());
        return Result.success();
    }
}
