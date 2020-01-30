package com.notes.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TagWithNoteParam extends TagParam {


    @NotBlank
    private String noteId;

    public TagWithNoteParam(String tag, String noteId) {
        this.tag=tag;
        this.noteId=noteId;
    }

    public String getNoteId() {
        return noteId;
    }
}
