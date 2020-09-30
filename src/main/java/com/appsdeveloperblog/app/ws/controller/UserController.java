package com.appsdeveloperblog.app.ws.controller;

import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("users")
public class UserController {
    @Autowired
    private UserService userService;;

    @Autowired
    AddressService addressesService;

    @GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        ModelMapper modelMapper = new ModelMapper();
        returnValue = modelMapper.map(userDto, UserRest.class);
        return returnValue;
    }

    @PostMapping(
            consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();
        //BeanUtils.copyProperties(userDetails, userDto);
        userDto = new ModelMapper().map(userDetails, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);
        //BeanUtils.copyProperties(createdUser, returnValue);
        returnValue = new ModelMapper().map(createdUser, UserRest.class);
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
        userDto = new ModelMapper().map(userDetails, UserDto.class);

        UserDto updateUser = userService.updateUser(id, userDto);
        returnValue = new ModelMapper().map(updateUser, UserRest.class);

        return returnValue;
    }

    @DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
//    @ApiImplicitParams({
//            @ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
//    })
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }


//    @ApiImplicitParams({
//            @ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
//    })
    @GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "2") int limit) {
        if(page>0) page--;
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

        Type listType = new TypeToken<List<UserRest>>() {
        }.getType();
        returnValue = new ModelMapper().map(users, listType);

//		for (UserDto userDto : users) {
//			UserRest userModel = new UserRest();
//			BeanUtils.copyProperties(userDto, userModel);
//			returnValue.add(userModel);
//		}

        return returnValue;
    }

    // http://localhost:8080/mobile-app-ws/users/jfhdjeufhdhdj/addressses
    @GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
    public List<AddressesRest> getUserAddresses(@PathVariable String id) {
        List<AddressesRest> addressesListRestModel = new ArrayList<>();

        List<AddressDTO> addressesDTO = addressesService.getAddresses(id);

        if (addressesDTO != null && !addressesDTO.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();
            addressesListRestModel = new ModelMapper().map(addressesDTO, listType);

//            for (AddressesRest addressRest : addressesListRestModel) {
//                Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId()))
//                        .withSelfRel();
//                addressRest.add(addressLink);
//
//                Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
//                addressRest.add(userLink);
//            }
        }

        return addressesListRestModel;
    }

    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
    public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

        AddressDTO addressesDto = addressesService.getAddress(addressId);

        ModelMapper modelMapper = new ModelMapper();
        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
        Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

        AddressesRest returnValue = modelMapper.map(addressesDto, AddressesRest.class);
//
//        returnValue.add(addressLink);
//        returnValue.add(userLink);
//        returnValue.add(addressesLink);

        return EntityModel.of(returnValue, Arrays.asList(userLink,addressesLink,addressLink));
    }
}
