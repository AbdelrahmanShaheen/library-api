package com.onesolution.library.service;

import com.onesolution.library.dto.BookRequest;
import com.onesolution.library.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    public void addBook(BookRequest bookRequest);

    public Page<BookResponse> getAllBooks(Pageable page);

    public BookResponse getBookById(Long id);

    public void updateBook(Long id, BookRequest bookRequest);

    public void deleteBook(Long id);

    public Page<BookResponse> searchBooksBy(String title, String genre, Pageable page);
}
