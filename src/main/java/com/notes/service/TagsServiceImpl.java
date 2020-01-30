package com.notes.service;

import com.notes.exception.ResourceNotFoundException;
import com.notes.model.Note;
import com.notes.model.Tag;
import com.notes.repository.NoteRepository;
import com.notes.repository.TagRepository;
import com.notes.request.TagWithNoteParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class TagsServiceImpl implements TagsService{

    TagRepository tagRepository;

    NoteRepository noteRepository;

    @Autowired
    public TagsServiceImpl(
            TagRepository tagRepository,
            NoteRepository noteRepository)
    {
        this.tagRepository = tagRepository;
        this.noteRepository=noteRepository;
    }

    @Override
    public Tag saveTag(TagWithNoteParam requestedTag, String userId){

        Tag tag=new Tag();
        tag.setTag(requestedTag.getTag());
        tag.setUserId(userId);

        Optional<Note> optionalNote=noteRepository.findById(requestedTag.getNoteId());
        if(!optionalNote.isPresent()){
            throw new ResourceNotFoundException();
        }

        Note note=optionalNote.get();

        if(!this.isValidOwner(userId, note)){
            throw new ResourceNotFoundException();
        }

        tag.getNotes().add(note);
        tagRepository.save(tag);

        note.addTag(tag);
        noteRepository.save(note);

        return tag;
    }

    protected boolean isValidOwner(String userId, Note note){
        return userId.equals(note.getUserId());
    }
}
