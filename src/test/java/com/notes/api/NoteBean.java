package com.notes.api;

import com.notes.model.Tag;
import com.notes.request.TagParam;
import lombok.AllArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
public class NoteBean {

    private String note;

    private Set<TagParam> tags = new HashSet<>();

    public void setNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return this.note;
    }

    public void setTags(Set<TagParam> tags) {
        this.tags = tags;
    }

    public Set<TagParam> getTags() {
        return tags;
    }
}
