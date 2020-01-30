package com.notes.api;

import com.notes.exception.InvalidRequestException;
import com.notes.exception.ResourceNotFoundException;
import com.notes.getter.TagGetter;
import com.notes.model.Tag;
import com.notes.model.User;
import com.notes.repository.TagRepository;
import com.notes.request.TagParam;
import com.notes.request.TagWithNoteParam;
import com.notes.service.TagsService;
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
@RequestMapping(path = "/tags")
public class TagsApi {

    TagRepository tagRepository;

    TagsService tagsService;

    @Autowired
    public TagsApi(TagRepository tagRepository, TagsService tagsService){
        this.tagRepository = tagRepository;
        this.tagsService=tagsService;
    }

    @GetMapping
    public ResponseEntity getTags(@AuthenticationPrincipal User user) {

        List<Tag> tags= tagRepository.findAllByUserId(user.getId());

        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {{
                    put("tags",tags.stream()
                            .map(e->new TagGetter(e.getId(),e.getTag()))
                    );
                }}
        );
    }
    @GetMapping(path={"/{id}"})
    public ResponseEntity getTag(@PathVariable("id") String id, @AuthenticationPrincipal User user){

        Optional<Tag> optionalTag= tagRepository.findById(id);

        if(!optionalTag.isPresent()){
            throw new ResourceNotFoundException();
        }

        Tag tag=optionalTag.get();

        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {{
                    put("tag",tag);
                }}
        );
    }

    @DeleteMapping(path={"/{id}"})
    public ResponseEntity removeTag(@PathVariable("id") String id){
        Optional<Tag> tag= tagRepository.findById(id);
        if(!tag.isPresent()){
            throw new ResourceNotFoundException();
        }
        tagRepository.delete(tag.get());

        return ResponseEntity.ok("");
    }

    @PostMapping
    public ResponseEntity addTag(
            @Valid @RequestBody TagWithNoteParam requestedTag,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user
    ){
        if(bindingResult.hasErrors()){
            throw new InvalidRequestException(bindingResult);
        }

        Tag tag=this.tagsService.saveTag(requestedTag, user.getId());

        return ResponseEntity.status(201).body(
                new HashMap<String, Object>() {{
                    put("tag", new TagGetter(tag.getId(),tag.getTag()));
                }}
        );
    }

    @PatchMapping(path={"/{id}"})
    public ResponseEntity updateTag(
            @PathVariable("id") String id,
            @Valid @RequestBody TagParam tagParam,
            BindingResult bindingResult
    ){

        if(bindingResult.hasErrors()){
            throw new InvalidRequestException(bindingResult);
        }

        Optional<Tag> optionalTag= tagRepository.findById(id);
        if(!optionalTag.isPresent()){
            throw new ResourceNotFoundException();
        }
        Tag tag=optionalTag.get();
        tag.setTag(tagParam.getTag());
        tagRepository.save(tag);
        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {{
                    put("tag", new TagGetter(tag.getId(),tag.getTag()));
                }}
        );
    }

}
