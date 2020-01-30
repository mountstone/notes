package com.notes.getter;

import lombok.Getter;

@Getter
public class TagGetter {

    private String id;

    private String tag;

    public TagGetter(
            String id,
            String tag
    ){
        this.id=id;
        this.tag=tag;
    }
}
