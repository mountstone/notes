package com.notes.api;

import com.notes.exception.InvalidRequestException;
import com.notes.exception.ResourceNotFoundException;
import com.notes.getter.NoteGetter;
import com.notes.model.Note;
import com.notes.model.User;
import com.notes.repository.NoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/notes")
@Slf4j
public class NotesApi {

    NoteRepository noteRepository;

    @Autowired
    public NotesApi(NoteRepository noteRepository){
        this.noteRepository=noteRepository;
    }

    @GetMapping
    public ResponseEntity getNotes(@AuthenticationPrincipal User user) {

        List<Note> notes=noteRepository.findByUserId(user.getId());

        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {{
                    put("notes",notes);
                }}
        );
    }

    @GetMapping(path = {"/{id}"})
    public ResponseEntity getNote(
            @PathVariable("id") String id,
            @AuthenticationPrincipal User user
    ){

        Optional<Note> optionalNote=noteRepository.findById(id);
        if(!optionalNote.isPresent()){
            throw new ResourceNotFoundException();
        }
        Note note=optionalNote.get();

        if(!note.getUserId().equals(user.getId())){
            throw new ResourceNotFoundException();
        }

        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {{
                    put("note",new NoteGetter(
                            note.getId(),
                            note.getNote(),
                            note.tags())
                    );
                }}
        );
    }

    @DeleteMapping(path={"/{id}"})
    public ResponseEntity removeNote(@PathVariable("id") String id){
        Optional<Note> note=noteRepository.findById(id);
        if(!note.isPresent()){
            throw new ResourceNotFoundException();
        }
        noteRepository.delete(note.get());

        return ResponseEntity.ok("");
    }

    @PostMapping
    public ResponseEntity addNote(
            @Valid @RequestBody Note note,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user
    ){
        if(bindingResult.hasErrors()){
            throw new InvalidRequestException(bindingResult);
        }
        note.setUserId(user.getId());
        noteRepository.save(note);

        return ResponseEntity.ok("");
    }

    @PatchMapping(path={"/{id}"})
    public ResponseEntity updateNote(
            @PathVariable("id") String id,
            @Valid @RequestBody Note requestedNote,
            BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            throw new InvalidRequestException(bindingResult);
        }

        Optional<Note> optional=noteRepository.findById(id);
        if(!optional.isPresent()){
            throw new ResourceNotFoundException();
        }
        requestedNote.setId(id);
        noteRepository.save(requestedNote);

        return ResponseEntity.ok("");
    }

}
