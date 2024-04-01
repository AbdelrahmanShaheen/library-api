package com.onesolution.library.mapper.Impl;

import com.onesolution.library.dto.AuthorResponse;
import com.onesolution.library.dto.BookRequest;
import com.onesolution.library.dto.BookResponse;
import com.onesolution.library.entity.Author;
import com.onesolution.library.entity.Book;
import com.onesolution.library.mapper.BookMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public Book toEntity(BookRequest bookRequest) {
        if ( bookRequest == null ) {
            return null;
        }

        Book.BookBuilder book = Book.builder();

        book.availableQuantity( bookRequest.getQuantity() );
        book.title( bookRequest.getTitle() );
        book.description( bookRequest.getDescription() );
        book.quantity( bookRequest.getQuantity() );
        book.genre( bookRequest.getGenre() );

        book.publishedDate( LocalDateTime.now() );

        return book.build();
    }

    @Override
    public BookResponse toDto(Book book) {
        if ( book == null ) {
            return null;
        }

        BookResponse.BookResponseBuilder bookResponse = BookResponse.builder();

        bookResponse.availableQuantity( book.getQuantity() );
        bookResponse.title( book.getTitle() );
        bookResponse.description( book.getDescription() );
        bookResponse.author( authorToAuthorResponse( book.getAuthor() ) );
        bookResponse.quantity( book.getQuantity() );
        bookResponse.genre( book.getGenre() );

        return bookResponse.build();
    }

    protected AuthorResponse authorToAuthorResponse(Author author) {
        if ( author == null ) {
            return null;
        }

        AuthorResponse.AuthorResponseBuilder authorResponse = AuthorResponse.builder();

        authorResponse.name( author.getName() );
        authorResponse.biography( author.getBiography() );

        return authorResponse.build();
    }
}