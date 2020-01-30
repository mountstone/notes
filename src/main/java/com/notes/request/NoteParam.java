package com.notes.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;

@Getter
@JsonRootName("note")
@NoArgsConstructor
public class NoteParam {

    @NotBlank(message = "can't be empty")
    @Length(max = 4000)
    private String note;

    public String getNote(){
        return this.note;
    }
}
