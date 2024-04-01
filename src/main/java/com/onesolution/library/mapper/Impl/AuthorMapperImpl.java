package com.onesolution.library.mapper.Impl;

import com.onesolution.library.dto.AuthorRequest;
import com.onesolution.library.dto.AuthorResponse;
import com.onesolution.library.entity.Author;
import com.onesolution.library.mapper.AuthorMapper;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapperImpl implements AuthorMapper {

    @Override
    public Author toEntity(AuthorRequest authorRequest) {
        if ( authorRequest == null ) {
            return null;
        }

        Author.AuthorBuilder author = Author.builder();

        author.name( authorRequest.getName() );
        author.biography( authorRequest.getBiography() );

        return author.build();
    }

    @Override
    public AuthorResponse toResponse(Author author) {
        if ( author == null ) {
            return null;
        }

        AuthorResponse.AuthorResponseBuilder authorResponse = AuthorResponse.builder();

        authorResponse.name( author.getName() );
        authorResponse.biography( author.getBiography() );

        return authorResponse.build();
    }
}