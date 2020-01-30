package com.notes.model;

import com.notes.NotesApplication;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NotesApplication.class)


public class NoteTest {

    private static Tag getTag(){
        return new Tag("1","1","tag");
    }

    private static Note getNoteWithTagsObject() {
        Set<Tag> tags=new HashSet<>();
        Tag tag=getTag();
        tags.add(tag);
        return new Note("1","1","foo",tags);
    }

    @Test
    public void givenConstructorNote_hasValidGetters(){
        Note note = new Note("1","1","foo");
        assertTrue("id is 1", "1".equals(note.getId()));
        assertTrue("user id is 1", "1".equals(note.getUserId()));
        assertTrue("note is foo", "foo".equals(note.getNote()));

        Note noteWithTags = this.getNoteWithTagsObject();
        assertThat(noteWithTags.tags(), Matchers.instanceOf(List.class));
        assertFalse("tags is not empty",noteWithTags.tags().isEmpty());
    }

    @Test
    public void entityContainTags_afterAddTag(){
        Note note = new Note("1","1","foo");
        note.addTag(getTag());
        assertFalse("tags is not empty",note.tags().isEmpty());
        assertEquals(1,note.tags().size());
    }

    @Test
    public void tagRemove_isEmptyTagsList()
    {
        Set<Tag> tags=new HashSet<>();
        Tag tag=getTag();
        tags.add(tag);
        Note note= new Note("1","1","foo",tags);
        assertTrue("note contains not empty tags list",note.tags().size()>0);
        note.removeTag(tag);
        assertTrue("tags list is empty",note.tags().isEmpty());
    }

}


