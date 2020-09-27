package com.appsdeveloperblog.app.ws.controller;

import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserService userService;;

    @GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, returnValue);
        return returnValue;
    }

    @PostMapping(
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);
        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, returnValue);
        return returnValue;
    }

    @PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE })
//    @ApiImplicitParams({
//            @ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
//    })
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();

        UserDto userDto = new UserDto();
        //userDto = new ModelMapper().map(userDetails, UserDto.class);
        BeanUtils.copyProperties(userDetails, userDto);
        UserDto updateUser = userService.updateUser(id, userDto);
        //returnValue = new ModelMapper().map(updateUser, UserRest.class);
        BeanUtils.copyProperties(updateUser, returnValue);
        return returnValue;
    }

    @DeleteMapping
    public String deleteUser(){
        return "delete user called";
    }
}
