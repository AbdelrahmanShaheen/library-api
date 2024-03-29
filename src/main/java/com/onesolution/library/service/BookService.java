package com.onesolution.library.service;

import com.onesolution.library.dto.BookRequest;
import com.onesolution.library.entity.Author;
import com.onesolution.library.entity.Book;
import com.onesolution.library.exception.ConflictException;
import com.onesolution.library.exception.ResourceNotFoundException;
import com.onesolution.library.mapper.BookMapper;
import com.onesolution.library.repository.AuthorRepository;
import com.onesolution.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;
    public void addBook(BookRequest bookRequest) {
        boolean isBookPresent = bookRepository.existsByTitle(bookRequest.getTitle());
        if (isBookPresent) {
            throw new ConflictException("Book with title " + bookRequest.getTitle() + " already exists");
        }
        Author author = authorRepository.findById(bookRequest.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author with id " + bookRequest.getAuthorId() + " does not exist"));
        Book book = bookMapper.toEntity(bookRequest);
        book.setAuthor(author);
        bookRepository.save(book);
    }
}
