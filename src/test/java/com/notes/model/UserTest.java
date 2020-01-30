package com.notes.model;

import com.notes.NotesApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotesApplication.class)


public class UserTest {


    @Test
    public void givenConstructorUser_hasValidGetters(){
        String username="foo";
        String password="bar";

        User user = new User(username,password);
        user.setId("1");
        assertTrue("id is 1", "1".equals(user.getId()));
        assertTrue("username is set foo", username.equals(user.getUsername()));
        assertTrue("password is bar", password.equals(user.getPassword()));

    }


}


