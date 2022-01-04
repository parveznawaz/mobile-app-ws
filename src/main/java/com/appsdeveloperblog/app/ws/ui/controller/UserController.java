package com.appsdeveloperblog.app.ws.ui.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetModel;
import com.appsdeveloperblog.app.ws.ui.model.request.PasswordResetRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.AddressesRest;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;

@RestController
@RequestMapping("users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	AddressService addressesService;

	
	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        //ModelMapper modelMapper = new ModelMapper();
        //returnValue = modelMapper.map(userDto, UserRest.class);
        BeanUtils.copyProperties(userDto, returnValue);
        return returnValue;
    }
	

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
            MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
		//UserRest returnValue = new UserRest();
		//UserDto userDto = new UserDto();
		
		//BeanUtils.copyProperties(userDetails, userDto);
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto createdUser = userService.createUser(userDto);
		
		//BeanUtils.copyProperties(createdUser, returnValue);
		UserRest returnValue = modelMapper.map(createdUser, UserRest.class);
		
		return returnValue;
		
	}
	
	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE })
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
	
	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        return returnValue;
	}
	
	
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "limit", defaultValue = "2") int limit) {
        List<UserRest> returnValue = new ArrayList<>();

        List<UserDto> users = userService.getUsers(page, limit);

//        Type listType = new TypeToken<List<UserRest>>() {
//        }.getType();
        //returnValue = new ModelMapper().map(users, listType);

		for (UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}

        return returnValue;
    }
	
	
//	@ApiImplicitParams({
//		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
//	})
	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public  CollectionModel<AddressesRest> getUserAddresses(@PathVariable String id) {
		List<AddressesRest> addressesListRestModel = new ArrayList<>();

		List<AddressDTO> addressesDTO = addressesService.getAddresses(id);
	

		if (addressesDTO != null && !addressesDTO.isEmpty()) {
			java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {
			}.getType();
			addressesListRestModel = new ModelMapper().map(addressesDTO, listType);

			for (AddressesRest addressRest : addressesListRestModel) {
				Link addressLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId()))
						.withSelfRel();
				addressRest.add(addressLink);

				Link userLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUser(id)).withRel("user");
				addressRest.add(userLink);
				
			}

		}
		
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(id)).withSelfRel();
		return  CollectionModel.of(addressesListRestModel, userLink, selfLink);
	}
	
	
//	@ApiImplicitParams({
//		@ApiImplicitParam(name="authorization", value="${userController.authorizationHeader.description}", paramType="header")
//	})
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

		AddressDTO addressesDto = addressesService.getAddress(addressId);

		ModelMapper modelMapper = new ModelMapper();
		AddressesRest returnValue = modelMapper.map(addressesDto, AddressesRest.class);
		
		Link userLink =  WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
		Link userAddressLink =  WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		
		Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();

		return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressLink, selfLink));
	}

	@GetMapping(path = "/email-verification", produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public OperationStatusModel verifyEmailToken(@RequestParam(value="token") String token){
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		boolean isVerified = userService.verifyEmailToken(token);
		if(isVerified){
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}else
		{
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		return  returnValue;
	}
	
	@PostMapping(path = "/password-reset-request",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult)
        {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }
	
	
	@PostMapping(path = "/password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());

        if(operationResult)
        {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }

	
}
