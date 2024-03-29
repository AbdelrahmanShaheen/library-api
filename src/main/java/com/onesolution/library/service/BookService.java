package com.onesolution.library.service;

import com.onesolution.library.dto.BookRequest;
import com.onesolution.library.dto.BookResponse;
import com.onesolution.library.entity.Author;
import com.onesolution.library.entity.Book;
import com.onesolution.library.exception.ConflictException;
import com.onesolution.library.exception.RequestValidationException;
import com.onesolution.library.exception.ResourceNotFoundException;
import com.onesolution.library.mapper.BookMapper;
import com.onesolution.library.repository.AuthorRepository;
import com.onesolution.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new ResourceNotFoundException("Author with id [%s] not found".formatted(bookRequest.getAuthorId())));
        Book book = bookMapper.toEntity(bookRequest);
        book.setAuthor(author);
        bookRepository.save(book);
    }

    public Page<BookResponse> getAllBooks(Pageable page) {
        return bookRepository.findAll(page).map(bookMapper::toDto);
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id [%s] does not exist".formatted(id)));
        return bookMapper.toDto(book);
    }

    public void updateBook(Long id, BookRequest bookRequest) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id [%s] does not exist".formatted(id)));

        boolean changes = false;

        String title = bookRequest.getTitle();
        String description = bookRequest.getDescription();
        int quantity = bookRequest.getQuantity();
        String genre = bookRequest.getGenre();
        Long authorId = bookRequest.getAuthorId();

        if (title != null && !title.equals(book.getTitle())) {
            book.setTitle(title);
            boolean isBookPresent = bookRepository.existsByTitle(title);
            if (isBookPresent) {
                throw new ConflictException("Book with title: " + title + ". already exists");
            }
            changes = true;
        }
        if (description != null && !description.equals(book.getDescription())) {
            book.setDescription(description);
            changes = true;
        }
        if (quantity != book.getQuantity()) {
            if(quantity < 0) {
                throw new RequestValidationException("Quantity cannot be negative");
            }
            book.setQuantity(quantity);
            changes = true;
        }
        if (genre != null && !genre.equals(book.getGenre())) {
            book.setGenre(genre);
            changes = true;
        }
        if (authorId != null && !authorId.equals(book.getAuthor().getId())) {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author with id [%s] does not exist".formatted(authorId)));
            book.setAuthor(author);
            changes = true;
        }
        if (!changes) {
            throw new RequestValidationException("No changes detected");
        }
        bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id [%s] does not exist".formatted(id)));
        bookRepository.delete(book);
    }

    public Page<BookResponse> searchBooksBy(String title, String genre, Pageable page) {
        if(title != null) {
            return bookRepository.findByTitle(title, page).map(bookMapper::toDto);
        }
        if(genre != null) {
            return bookRepository.findByGenre(genre, page).map(bookMapper::toDto);
        }
        return getAllBooks(page);
    }
}
