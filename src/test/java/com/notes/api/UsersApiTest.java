package com.notes.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.notes.model.User;
import com.notes.repository.UserRepository;
import com.notes.config.WebSecurityConfig;
import com.notes.service.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UsersApi.class)
@Import({
        WebSecurityConfig.class
})
@Slf4j
public class UsersApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    JwtService jwtService;

    @MockBean
    UserService userService;

    @MockBean
    EncryptService encryptService;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    public void should_create_user_success() throws Exception {
        Mockito.when(jwtService.toToken(any())).thenReturn("123");

        mvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(this.createUserPayload("foo","123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        ).andExpect(status().is(201));

    }

    @Test
    public void should_show_duplicated_error_when_create_user() throws Exception {

        String username="foo";
        String password="123";
        mockAuthorization(username,password);

        mvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(this.createUserPayload(username,password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().is(422))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(
                        containsString("duplicated username"))
                );

    }

    @Test
    public void should_show_error_message_for_blank_username() throws Exception {

        String username="";
        String password="123";

        Mockito.when(jwtService.toToken(any())).thenReturn("123");

        mvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .content(this.createUserPayload(username,password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        )
                .andExpect(status().is(422))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(
                        containsString("Username can't be empty"))
                );

    }

    @Test
    public void shouldnt_have_access_to_users_endpoint_when_no_jwt() throws Exception {

        mvc.perform(
                MockMvcRequestBuilders.get("/users")
        ).andExpect(status().is(401));
    }

    @Test
    public void should_see_user_details_when_logged_in() throws Exception {

        this.mockAuthorization("foo","123");

        mvc.perform(
                MockMvcRequestBuilders.get("/users/123").header("Authorization","Bearer 123")
            ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{'user':{'id':'123','username':'foo'}}"));
    }

    @Test
    public void post_user_login_error_when_invalid_credentials() throws Exception{

        mvc.perform(
                MockMvcRequestBuilders.post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.createUserPayload("username","password"))
        ).andExpect(status().is(422))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(
                        containsString("invalid username or password")
                ));
    }

    @Test
    public void post_user_login_success() throws Exception{
        String username="username";
        String password="password";
        this.mockAuthorization(username,password);
        Mockito.when(encryptService.check(password,password)).thenReturn(true);

        mvc.perform(
                MockMvcRequestBuilders.post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.createUserPayload(username,password))
        ).andExpect(status().is(200));

    }


    @Test
    public void get_user_error_when_entry_not_exists() throws Exception {

        this.mockAuthorization("foo","123");
        mvc.perform(
                MockMvcRequestBuilders.get("/users/321")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer 123")
        ).andExpect(status().is(404));
    }

    @Test
    public void remove_user_error_when_entry_not_exists() throws Exception {

        this.mockAuthorization("foo","123");
        mvc.perform(
                MockMvcRequestBuilders.delete("/users/321")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer 123")
        ).andExpect(status().is(404));
    }

    @Test
    public void remove_user_success() throws Exception {

        this.mockAuthorization("foo","123");
        mvc.perform(
                MockMvcRequestBuilders.delete("/users/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer 123")
        ).andExpect(status().is(200));
    }


    private void mockAuthorization(String username, String password)
    {
        User user = new User(username, password);
        user.setId("123");

        Optional<User> optionalUser=Optional.of(user);
        Mockito.when(userRepository.findByUsername(username)).thenReturn(optionalUser);
        Mockito.when(userRepository.findById("123")).thenReturn(optionalUser);
        Mockito.when(jwtService.toToken(any())).thenReturn("123");
        Mockito.when(jwtService.getSubjectFromToken(any())).thenReturn(
                Optional.of("123")
        );
    }

    private String createUserPayload(String username, String password) throws JsonProcessingException {
        UserBean userBean=new UserBean(
                username, password
        );

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(userBean);
    }



}
