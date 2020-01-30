package com.notes.config;

import com.notes.api.UsersApi;
import com.notes.model.User;
import com.notes.repository.UserRepository;
import com.notes.service.EncryptService;
import com.notes.service.JwtService;
import com.notes.service.UserService;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UsersApi.class)
@Import({
        WebSecurityConfig.class
})
@Slf4j
public class WebSecurityConfigTest {
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


    @Test
    public void givenRequest_hasValidAuthenticationEntryPoint() throws Exception {

        mvc.perform(
                MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        ).andExpect(
                status().isUnauthorized()
        ).andExpect(
                status().reason("Jwt authentication failed")
        );

    }

    @Test
    public void givenRequest_hasPostRequestToUsersIsBadRequest() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        ).andExpect(
                status().isBadRequest()
        );
    }
}
