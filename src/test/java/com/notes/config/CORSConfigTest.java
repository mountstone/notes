package com.notes.config;

import com.notes.api.UsersApi;
import com.notes.model.User;
import com.notes.repository.UserRepository;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UsersApi.class)
@Import({
        WebSecurityConfig.class
})
@Slf4j
public class CORSConfigTest {
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
    public void givenPutMethod_whenMethodIsNotAllowed() throws Exception {
        User user = new User("username", "password");
        user.setId("123");

        Optional<User> optionalUser=Optional.of(user);
        Mockito.when(userRepository.findByUsername("username")).thenReturn(optionalUser);
        Mockito.when(userRepository.findById("123")).thenReturn(optionalUser);
        Mockito.when(jwtService.toToken(any())).thenReturn("123");
        Mockito.when(jwtService.getSubjectFromToken(any())).thenReturn(
                Optional.of("123")
        );

        Mockito.when(jwtService.toToken(any())).thenReturn("123");

        mvc.perform(
                MockMvcRequestBuilders.put("/users")
                        .header("Authorization","Bearer 123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        ).andExpect(status().isMethodNotAllowed());

    }

}
