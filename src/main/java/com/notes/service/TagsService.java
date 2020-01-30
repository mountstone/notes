package com.notes.service;

import com.notes.model.Tag;
import com.notes.request.TagWithNoteParam;
import org.springframework.stereotype.Service;

@Service
public interface TagsService {
    Tag saveTag(TagWithNoteParam requestedTag, String userId);

}
