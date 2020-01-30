package com.notes.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagParam {

    @NotBlank
    @Length(max = 50)
    protected String tag;

    public String getTag() {
        return tag;
    }

}
