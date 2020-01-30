package com.notes.repository;

import com.notes.NotesApplication;
import com.notes.model.Tag;
import com.notes.model.User;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotesApplication.class)
@ContextConfiguration(locations = {"classpath:test-db-context.xml"})
@Slf4j
public class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    public void givenUserIdWhenFindAllByUserId_ThenTagsListShouldReturn() {
        List<Tag> results = tagRepository.findAllByUserId("1");
        assertEquals(1, results.size());
    }

    @Test
    public void givenTagId_WhenFindById_ThenOptionalTagShouldReturn() {
        Optional<Tag> result = tagRepository.findById("1");
        assertTrue(result.isPresent());
    }

    @Test
    public void whenSave_ThenTagShouldReturn() {
        Tag tag=new Tag("2","1","foo tag");

        assertThat(tagRepository.save(tag), Matchers.instanceOf(Tag.class));
        assertEquals(2,tagRepository.findAll().size());
    }

}

