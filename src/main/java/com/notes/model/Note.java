package com.notes.model;

import com.notes.getter.TagGetter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @Column
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column
    @NotBlank
    @Length(max = 4000)
    private String note;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "notes_tags",
            joinColumns = { @JoinColumn(name = "note_id") },
            inverseJoinColumns = { @JoinColumn(name = "tag_id") }
    )

    private Set<Tag> tags = new HashSet<>();

    public Note(){};

    public Note(String id,String userId,String note){
        this.id=id;
        this.userId=userId;
        this.note=note;
    }

    public Note(String id,String userId,String note, Set<Tag> tags){
        this.id=id;
        this.userId=userId;
        this.note=note;
        this.tags=tags;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return this.note;
    }

    public String getUserId(){
        return this.userId;
    }

    public void setUserId(String userId){
        this.userId=userId;
    }


    public void addTag(Tag tag){
        this.tags.add(tag);
        tag.getNotes().add(this);
    }

    public void removeTag(Tag tag){
        this.tags.remove(tag);
        tag.getNotes().remove(this);
    }

    public List<TagGetter> tags() {
        return tags.stream().map(e -> new TagGetter(
                e.getId(),
                e.getTag()
            )).collect(Collectors.toList());
    }
}
