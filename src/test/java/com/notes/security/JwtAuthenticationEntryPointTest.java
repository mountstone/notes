package com.notes.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notes.api.NotesApi;
import com.notes.config.WebSecurityConfig;
import com.notes.repository.NoteRepository;
import com.notes.repository.UserRepository;
import com.notes.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest(NotesApi.class)
@Import({
        WebSecurityConfig.class
})
@Slf4j
public class JwtAuthenticationEntryPointTest {

    @MockBean
    NoteRepository noteRepository;

    @MockBean
    JwtTokenFilter jwtTokenFilter;

    @MockBean
    AuthenticationException authenticationException;

    @Test
    public void commenceHasValidError_whenProvidedRequest() throws IOException, ServletException {

        AuthenticationEntryPoint entryPoint= new JwtAuthenticationEntryPoint();
        MockHttpServletResponse response=new MockHttpServletResponse();
        entryPoint.commence(
                new MockHttpServletRequest(),
                response,
                authenticationException
        );

        assertSame("Jwt authentication failed",response.getErrorMessage());
    }

}
