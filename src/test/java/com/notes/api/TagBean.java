package com.notes.api;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TagBean {

    private String tag;

    private String noteId;

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

}
