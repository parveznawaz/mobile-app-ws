package com.appsdeveloperblog.app.ws.ui.controller;

import com.appsdeveloperblog.app.ws.service.impl.UserServiceImpl;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserControllerTest {
    
    @InjectMocks
    UserController userController;
    
    @Mock
    UserServiceImpl userService;

    UserDto userDto;
    final String USER_ID = "userid123";
    String addressId = "addressId123";
    String encryptedPassword = "encryptedPassword123";
    String password = "password123";
    String firstName = "parvez";
    String lastName = "nawaz";
    String emailAddress = "parvez.nawaz@gmail.com";
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setUserId(USER_ID);
        userDto.setEncryptedPassword(password);
        userDto.setEmail(emailAddress);
        userDto.setEmailVerificationToken("abdjd");
        userDto.setAddresses(getAddressesDto());
    }

    @Test
    void getUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        UserRest userRest = userController.getUser(USER_ID);

        assertNotNull(userRest);
        assertEquals(USER_ID, userRest.getUserId());
        assertEquals(userDto.getFirstName(), userRest.getFirstName());
        assertEquals(userDto.getLastName(), userRest.getLastName());
        assertEquals(userDto.getAddresses().size(), userRest.getAddresses().size());
    }

    private List<AddressDTO> getAddressesDto(){
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setType("shipping");
        addressDTO.setCity("Vancouver");
        addressDTO.setCountry("Canada");
        addressDTO.setPostalCode("ABC123");
        addressDTO.setStreetName("123 Street");

        AddressDTO billingAddressDto = new AddressDTO();
        billingAddressDto.setType("billing");
        billingAddressDto.setCity("Vancouver");
        billingAddressDto.setCountry("Canada");
        billingAddressDto.setPostalCode("ABC123");
        billingAddressDto.setStreetName("123 Street");


        ArrayList<AddressDTO> addresses = new ArrayList<>();
        addresses.add(addressDTO);
        addresses.add(billingAddressDto);
        return addresses;
    }
}