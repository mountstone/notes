package com.notes.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.notes.config.WebSecurityConfig;
import com.notes.model.Note;
import com.notes.model.Tag;
import com.notes.model.User;
import com.notes.repository.NoteRepository;
import com.notes.repository.UserRepository;
import com.notes.request.TagParam;
import com.notes.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(NotesApi.class)
@Import({
        WebSecurityConfig.class
})
@Slf4j
public class NotesApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    NoteRepository noteRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    JwtService jwtService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void should_get_notes_assigned_to_logged_user() throws Exception {
        this.mockAuthorization("foo","123");
        Note note=new Note("1","123","foo note");

        List<Note> notes=new ArrayList<Note>();
        notes.add(note);

        Mockito.when(noteRepository.findByUserId("123")).thenReturn(notes);

        mvc.perform(
                MockMvcRequestBuilders.get("/notes")
                        .header("Authorization","Bearer 123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        ).andExpect(status().isOk())
         .andExpect(content().json(
                 "{\"notes\":[{\"id\":\"1\",\"userId\":\"123\",\"note\":\"foo note\"}]}"
         ));

    }

    @Test
    public void get_note_which_doesnt_exists_error() throws Exception {

        mockAuthorization("foo","123");

        mvc.perform(
                MockMvcRequestBuilders.get("/notes/123")
                        .header("Authorization","Bearer 123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        ).andExpect(status().isNotFound());

    }
    @Test
    public void get_single_note_with_tags_success() throws Exception {

        this.mockAuthorization("foo","123");

        Set<Tag> tags=new HashSet<>();
        tags.add(new Tag("1","123","foo tag"));
        Optional<Note> note=Optional.of(
                new Note("1","123","foo note",tags)
        );

        Mockito.when(noteRepository.findById("1")).thenReturn(note);

        mvc.perform(
                    buildRequest(HttpMethod.GET,"/notes/1")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "{\"note\":{\"id\":\"1\",\"note\":\"foo note\",\"tags\":[{\"id\":\"1\",\"tag\":\"foo tag\"}]}}"
                ));
    }

    @Test
    public void remove_note_success() throws Exception {
        this.mockAuthorization("foo","123");

        Optional<Note> note=Optional.of(
                new Note("1","123","foo note")
        );

        Mockito.when(noteRepository.findById("1")).thenReturn(note);

        mvc.perform(
                buildRequest(HttpMethod.DELETE,"/notes/1")
        )
        .andExpect(status().isOk());
    }

    @Test
    public void remove_note_error_when_note_not_exists() throws Exception {
        this.mockAuthorization("foo","123");

        mvc.perform(
                buildRequest(HttpMethod.DELETE,"/notes/2")
        )
        .andExpect(status().isNotFound());
    }

    @Test
    public void add_note_success() throws Exception {

        this.mockAuthorization("foo","123");

        String payload=this.getNotePayload();
        mvc.perform(
                buildRequest(HttpMethod.POST,"/notes")
                        .content(payload)
        )
        .andExpect(status().is(200));
    }

    @Test
    public void add_note_error_when_unauthorized() throws Exception {

        mvc.perform(
                buildRequest(HttpMethod.POST,"/notes")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    public void add_note_error_when_invalid_payload() throws Exception {

        this.mockAuthorization("foo","123");

        mvc.perform(
                buildRequest(HttpMethod.POST,"/notes")
                        .content("")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void update_note_error_when_unauthorized() throws Exception {
        mvc.perform(
                buildRequest(HttpMethod.PATCH,"/notes/1")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void update_note_error_when_invalid_payload() throws Exception {
        this.mockAuthorization("foo","123");
        mvc.perform(
                buildRequest(HttpMethod.PATCH,"/notes/1")
                .content("")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void update_note_success() throws Exception {
        this.mockAuthorization("foo","123");

        String payload=this.getNotePayload();
        Mockito.when(noteRepository.findById("1")).thenReturn(Optional.of(
                new Note("1","123","foo note")
        ));

        mvc.perform(
                buildRequest(HttpMethod.PATCH,"/notes/1")
                        .content(payload)
        ).andExpect(status().isOk());
    }



    private String getNotePayload() throws JsonProcessingException {
        String note="foo note";
        Set<TagParam> tags=new HashSet<>();
        tags.add(new TagParam("foo tag"));

        NoteBean noteBean=new NoteBean(
                note, tags
        );

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(noteBean);
    }



    private MockHttpServletRequestBuilder buildRequest(HttpMethod method,String endpoint){
        return MockMvcRequestBuilders
                .request(method,endpoint)
                .header("Authorization","Bearer 123")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8");
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

}
