package com.notes.model;

import com.notes.NotesApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotesApplication.class)

public class TagTest {

    @Test
    public void givenConstructorTag_hasValidGetters(){
        String tagValue="foo";
        Tag tag = new Tag("1","1",tagValue);

        assertTrue("id is 1", "1".equals(tag.getId()));
        assertTrue("user id is set 1", "1".equals(tag.getUserId()));
        assertTrue("tag is foo", tagValue.equals(tag.getTag()));
    }

    @Test
    public void givenNote_hasNotEmptyNoteList(){
        Set<Note> noteList=new HashSet<>();
        Note note=new Note("1","1","note");
        noteList.add(note);

        Tag tag = new Tag("1","1","foo");
        assertTrue(tag.getNotes().isEmpty());
        tag.setNotes(noteList);
        assertFalse(tag.getNotes().isEmpty());
    }

}


