package com.appsdeveloperblog.app.ws.service.impl;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.io.entity.AddressEntity;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.repositories.PasswordResetTokenRepository;
import com.appsdeveloperblog.app.ws.repositories.UserRepository;
import com.appsdeveloperblog.app.ws.shared.AmazonSES;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDTO;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    AmazonSES amazonSES;

    UserEntity userEntity = new UserEntity();

    String userId = "userid123";
    String addressId = "addressId123";
    String encryptedPassword = "encryptedPassword123";
    String password = "password123";
    String firstName = "parvez";
    String lastName = "nawaz";
    String emailAddress = "parvez.nawaz@gmail.com";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        userEntity.setId(1L);
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(password);
        userEntity.setEmail(emailAddress);
        userEntity.setEmailVerificationToken("abdjd");
        userEntity.setAddresses(getAddressesEntity());
    }



    @Test
    void testGetUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser(emailAddress);

        assertNotNull(userDto);
        assertEquals(userEntity.getFirstName(), userDto.getFirstName());
    }

    @Test
    void testGetUser_UserNameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> {
                    userService.getUser(emailAddress);
                });
    }


    @Test
    void testCreateUser_CreateUserServiceException(){
        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName(userEntity.getFirstName());
        userDto.setLastName(userEntity.getLastName());
        userDto.setPassword(userEntity.getEncryptedPassword());
        userDto.setEmail(userEntity.getEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        assertThrows(UserServiceException.class,

                ()->{
                    userService.createUser(userDto);
                });
    }

    @Test
    void testCreateUser(){

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn(addressId);
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName(userEntity.getFirstName());
        userDto.setLastName(userEntity.getLastName());
        userDto.setPassword(userEntity.getEncryptedPassword());
        userDto.setEmail(userEntity.getEmail());


        UserDto storedUserDetails = userService.createUser(userDto);
        assertNotNull(storedUserDetails);
        assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
        assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
        assertNotNull(storedUserDetails);
        assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());

        verify(utils, times(storedUserDetails.getAddresses().size())).generateAddressId(30);
        verify(bCryptPasswordEncoder, times(1)).encode(userDto.getPassword());
        verify(userRepository, times(1)).save(any(UserEntity.class));

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

    private List<AddressEntity> getAddressesEntity(){
        List<AddressDTO> addresses = getAddressesDto();

        Type listType = new TypeToken<List<AddressEntity>>(){}.getType();
        return new ModelMapper().map(addresses, listType);

    }


}
