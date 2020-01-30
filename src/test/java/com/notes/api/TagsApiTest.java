package com.notes.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.notes.config.WebSecurityConfig;
import com.notes.model.Tag;
import com.notes.model.User;
import com.notes.repository.TagRepository;
import com.notes.repository.UserRepository;
import com.notes.request.TagParam;
import com.notes.request.TagWithNoteParam;
import com.notes.service.JwtService;
import com.notes.service.TagsService;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TagsApi.class)
@Import({
        WebSecurityConfig.class
})
@Slf4j
public class TagsApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    TagRepository tagRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    TagsService tagsService;

    @MockBean
    JwtService jwtService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void should_get_tags_assigned_to_logged_user() throws Exception {
        this.mockAuthorization("foo","123");

        Tag tag=new Tag("1","123","foo tag");

        List<Tag> tags=new ArrayList<Tag>();
        tags.add(tag);

        Mockito.when(tagRepository.findAllByUserId("123")).thenReturn(tags);

        mvc.perform(
                this.buildRequest(HttpMethod.GET,"/tags")
        ).andExpect(status().isOk())
         .andExpect(content().json(
                 "{'tags':[{'id':'1','tag':'foo tag'}]}"
         ));

    }

    @Test
    public void get_tags_error_when_unauthorized() throws Exception {
        mvc.perform(
                this.buildRequest(HttpMethod.GET,"/tags")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void get_tag_error_when_unauthorized() throws Exception {
        mvc.perform(
                this.buildRequest(HttpMethod.GET,"/tags/1")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void get_tag_error_when_tag_doesnt_exists() throws Exception {
        this.mockAuthorization("foo","123");

        mvc.perform(
                this.buildRequest(HttpMethod.GET,"/tags/1")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void get_tag_success() throws Exception {
        this.mockAuthorization("foo","123");

        Mockito.when(tagRepository.findById("1")).thenReturn(
            Optional.of(new Tag("1","123","foo tag"))
        );

        mvc.perform(
                this.buildRequest(HttpMethod.GET,"/tags/1")
        )
        .andExpect(status().isOk())
        .andExpect(content().json(
                "{'tag':{'id':'1','userId':'123','tag':'foo tag','notes':[]}}"
        ));
    }

    @Test
    public void delete_tag_error_when_unauthorized() throws Exception {

        mvc.perform(
                this.buildRequest(HttpMethod.DELETE,"/tags/1")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    public void delete_tag_error_when_tag_doesnt_exists() throws Exception {
        this.mockAuthorization("foo","123");
        mvc.perform(
                this.buildRequest(HttpMethod.DELETE,"/tags/1")
        )
        .andExpect(status().isNotFound());
    }

    @Test
    public void delete_tag_success() throws Exception{
        this.mockAuthorization("foo","123");

        Mockito.when(tagRepository.findById("1")).thenReturn(
                Optional.of(new Tag("1","123","foo tag"))
        );

        mvc.perform(
                this.buildRequest(HttpMethod.DELETE,"/tags/1")
        )
        .andExpect(status().isOk());
    }

    @Test
    public void add_tag_error_when_unauthorized() throws Exception {
        mvc.perform(
                this.buildRequest(HttpMethod.POST,"/tags")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    public void add_tag_error_when_invalid_payload() throws Exception {
        this.mockAuthorization("foo","123");
        mvc.perform(
                this.buildRequest(HttpMethod.POST,"/tags")
                    .content("")
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    public void add_tag_success() throws Exception {
        this.mockAuthorization("foo","123");

        String payload=this.getTagPayload();

        Mockito.when(tagsService.saveTag(
                any(),
                eq("123")
        )).thenReturn(
                new Tag("1","123","foo tag")
        );

        mvc.perform(
                this.buildRequest(HttpMethod.POST,"/tags")
                        .content(payload)
        ).andExpect(status().is(201))
         .andExpect(content().json(" {'tag':{'id':'1','tag':'foo tag'}}"));
    }

    @Test
    public void update_tag_error_when_unauthorized() throws Exception {
        mvc.perform(
                this.buildRequest(HttpMethod.PATCH,"/tags/1")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void update_tag_error_when_invalid_payload() throws Exception {
        this.mockAuthorization("foo","123");
        mvc.perform(
                this.buildRequest(HttpMethod.PATCH,"/tags/1")
                .content("")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void update_tag_error_when_tag_doesnt_exists() throws Exception {
        this.mockAuthorization("foo","123");

        TagParam tagParam=new TagParam(
                "foo tag"
        );

        Mockito.when(tagRepository.findById("1")).thenReturn(Optional.empty());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        mvc.perform(
                this.buildRequest(HttpMethod.PATCH,"/tags/1")
                        .content(ow.writeValueAsString(tagParam))
        ).andExpect(status().isNotFound());
    }

    @Test
    public void update_tag_success() throws Exception {
        this.mockAuthorization("foo","123");

        Tag tag=new Tag("1","123","tag");

        Mockito.when(tagRepository.findById("1")).thenReturn(
            Optional.of(new Tag("1","123","tag"))
        );

        Mockito.when(
                tagRepository.save(tag)
        ).thenReturn(tag);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        mvc.perform(
                this.buildRequest(HttpMethod.PATCH,"/tags/1")
                        .content(ow.writeValueAsString(new TagParam(
                                "foo tag"
                        )))
        ).andExpect(status().isOk())
         .andExpect(
                 content().json("{'tag':{'id':'1','tag':'foo tag'}}")
         );
    }

    private String getTagPayload() throws JsonProcessingException {
        TagWithNoteParam tagBean=new TagWithNoteParam(
                "foo tag","1"
        );

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(tagBean);
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

    private MockHttpServletRequestBuilder buildRequest(HttpMethod method,String endpoint){
        return MockMvcRequestBuilders
                .request(method,endpoint)
                .header("Authorization","Bearer 123")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8");
    }




}
