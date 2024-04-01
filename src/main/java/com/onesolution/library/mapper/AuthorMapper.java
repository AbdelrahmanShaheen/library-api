package com.onesolution.library.mapper;

import com.onesolution.library.dto.AuthorRequest;
import com.onesolution.library.dto.AuthorResponse;
import com.onesolution.library.entity.Author;

public interface AuthorMapper {
    Author toEntity(AuthorRequest authorRequest);
    AuthorResponse toResponse(Author author);
}
