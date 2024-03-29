package com.onesolution.library.mapper;

import com.onesolution.library.dto.AuthorRequest;
import com.onesolution.library.entity.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    Author toEntity(AuthorRequest authorRequest);
}
