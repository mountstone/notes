package com.notes.repository;

import com.notes.NotesApplication;
import com.notes.model.Note;
import lombok.extern.slf4j.Slf4j;
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
public class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Test
    public void whenFindAll_ThenNotesShouldReturn() {
        List<Note> results = noteRepository.findAll();
        assertEquals(1, results.size());
    }

    @Test
    public void givenId_WhenFindById_ThenNoteShouldReturn() {
        Optional<Note> result = noteRepository.findById("1");
        assertTrue(result.isPresent());
    }

    @Test
    public void givenUserId_WhenFindByUserId_ThenNoteListShouldReturn() {
        List<Note> results = noteRepository.findByUserId("1");
        assertEquals(1, results.size());
    }


}

