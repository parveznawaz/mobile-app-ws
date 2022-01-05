package com.appsdeveloperblog.app.ws.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UtilsTest {

    @Autowired
    Utils utils;

    @BeforeEach
    void setUp() {
    }

    @Test
    void generateUserId() {
        String userId = utils.generateUserId(30);
        String userId2 = utils.generateUserId(30);
        assertNotNull(userId);
        assertTrue(userId.length()==30);
        assertTrue(!userId.equalsIgnoreCase(userId2));
    }

    @Test
    void hasTokenNotExpired() {
        String token = utils.generateEmailVerificationToken("jlkjkl");
        assertNotNull(token);
        boolean tokenExpired = Utils.hasTokenExpired(token);
        assertFalse(tokenExpired);
    }

    @Test
    void hasTokenExpired() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqbGtqa2wiLCJleHAiOjE2NDA0OTMwMzB9.itmUNHQqD4TpSCM-1rAm3ugq4bvyRxvglHvNoyRWPsg_hi_AX3JoHMeHLewkK4KKffELL7c9OxwW-fhwbpC90Q";
        assertNotNull(token);
        boolean tokenExpired = Utils.hasTokenExpired(token);
        assertTrue(tokenExpired);
    }
}