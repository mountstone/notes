package com.notes.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@lombok.Getter
@JsonRootName("errors")
public class ErrorResource {
    private List<FieldErrorResource> fieldErrors;

    public ErrorResource(List<FieldErrorResource> fieldErrorResources) {
        this.fieldErrors = fieldErrorResources;
    }


}

