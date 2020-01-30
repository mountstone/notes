package com.notes.repository;

import com.notes.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Integer> {

    List<Note> findAll();

    Note save(Note note);

    List<Note> findByUserId(String userId);

    Optional<Note> findById(String id);

    void delete(Note note);
}