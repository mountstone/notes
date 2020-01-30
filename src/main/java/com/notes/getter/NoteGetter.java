package com.notes.getter;

import com.notes.model.Tag;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class NoteGetter {

    private String id;

    private String note;

    private  List<TagGetter> tags;

    public NoteGetter(
            String id,
            String note,
            List<TagGetter> tags
    ){
        this.id=id;
        this.note=note;
        this.tags=tags;

    }
}
