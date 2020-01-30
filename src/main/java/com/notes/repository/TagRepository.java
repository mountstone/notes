package com.notes.repository;

import com.notes.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Integer> {

    List<Tag> findAllByUserId(String userId);

    Optional<Tag> findById(String tagId);

    void delete(Tag tag);

    Tag save(Tag save);
}
