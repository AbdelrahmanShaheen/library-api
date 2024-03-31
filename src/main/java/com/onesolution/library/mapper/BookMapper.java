package com.onesolution.library.mapper;

import com.onesolution.library.dto.BookRequest;
import com.onesolution.library.dto.BookResponse;
import com.onesolution.library.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring" ,imports = LocalDateTime.class)

public interface BookMapper {
    @Mapping(target = "availableQuantity", source = "quantity")
    @Mapping(target = "publishedDate", expression = "java(LocalDateTime.now())")
    Book toEntity(BookRequest bookRequest);
    @Mapping(target = "availableQuantity", source = "quantity")
    BookResponse toDto(Book book);
}
