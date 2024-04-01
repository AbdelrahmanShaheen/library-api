package com.onesolution.library.mapper;

import com.onesolution.library.dto.BookRequest;
import com.onesolution.library.dto.BookResponse;
import com.onesolution.library.entity.Book;


public interface BookMapper {
    Book toEntity(BookRequest bookRequest);
    BookResponse toDto(Book book);
}
