package com.onesolution.library;

import com.onesolution.library.dto.AuthorRequest;
import com.onesolution.library.dto.AuthorResponse;
import com.onesolution.library.dto.BookResponse;
import com.onesolution.library.entity.Author;
import com.onesolution.library.entity.Book;
import com.onesolution.library.exception.RequestValidationException;
import com.onesolution.library.exception.ResourceNotFoundException;
import com.onesolution.library.mapper.AuthorMapper;
import com.onesolution.library.mapper.BookMapper;
import com.onesolution.library.mapper.Impl.AuthorMapperImpl;
import com.onesolution.library.mapper.Impl.BookMapperImpl;
import com.onesolution.library.repository.AuthorRepository;
import com.onesolution.library.repository.BookRepository;
import com.onesolution.library.service.AuthorService;
import com.onesolution.library.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthorServiceTest {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;
    private final AuthorService underTest;

    public AuthorServiceTest(){
        authorRepository = mock(AuthorRepository.class);
        bookRepository = mock(BookRepository.class);
        authorMapper = new AuthorMapperImpl();
        bookMapper = new BookMapperImpl();
        underTest = new AuthorServiceImpl(authorRepository, bookRepository, authorMapper, bookMapper);
    }
    @Test
    public void createAuthorShouldSuccess(){
        // given
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Author Name");
        authorRequest.setBiography("Author Biography");

        // when
        underTest.createAuthor(authorRequest);

        // then
        verify(authorRepository).save(any());
    }
    @Test
    public void getAllAuthorsShouldReturnPageOfAuthors() {
        // Given
        Author author = new Author();
        author.setName("Author Name");
        author.setBiography("Author Biography");
        List<Author> authors = Collections.singletonList(author);
        Page<Author> authorPage = new PageImpl<>(authors);

        when(authorRepository.findAll(any(Pageable.class))).thenReturn(authorPage);

        // When
        Page<AuthorResponse> result = underTest.getAllAuthors(PageRequest.of(0, 1));

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals("Author Name", result.getContent().get(0).getName());
        assertEquals("Author Biography", result.getContent().get(0).getBiography());
    }
    @Test
    public void getAllAuthorsShouldReturnEmptyPageWhenNoAuthors() {
        // Given
        Page<Author> authorPage = Page.empty();

        when(authorRepository.findAll(any(Pageable.class))).thenReturn(authorPage);

        // When
        Page<AuthorResponse> result = underTest.getAllAuthors(PageRequest.of(0, 1));

        // Then
        assertEquals(0, result.getContent().size());
    }
    @Test
    public void getAuthorByIdShouldReturnAuthorResponseWhenIdExists() {
        // Given
        Long id = 1L;
        Author author = new Author();
        author.setName("Author Name");
        author.setBiography("Author Biography");

        when(authorRepository.findById(id)).thenReturn(Optional.of(author));

        // When
        AuthorResponse result = underTest.getAuthorById(id);

        // Then
        assertEquals("Author Name", result.getName());
        assertEquals("Author Biography", result.getBiography());
    }
    @Test
    public void getAuthorByIdShouldThrowExceptionWhenIdDoesNotExist() {
        // Given
        Long id = 1L;

        when(authorRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> underTest.getAuthorById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author with id [1] not found");
    }
    @Test
    public void updateAuthorShouldUpdateAuthorWhenIdExistsAndRequestHasChanges() {
        // Given
        Long id = 1L;
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("New Author Name");
        authorRequest.setBiography("New Author Biography");

        Author author = new Author();
        author.setName("Author Name");
        author.setBiography("Author Biography");

        when(authorRepository.findById(eq(id))).thenReturn(Optional.of(author));
        when(authorRepository.save(any())).thenReturn(author);
        // When
        underTest.updateAuthor(id, authorRequest);

        // Then
        verify(authorRepository).save(author);
    }

    @Test
    public void updateAuthorShouldThrowExceptionWhenIdDoesNotExist() {
        // Given
        Long id = 1L;
        AuthorRequest authorRequest = new AuthorRequest();

        when(authorRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy( () -> underTest.updateAuthor(id, authorRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author with id [1] not found");
    }

    @Test
    public void updateAuthorShouldThrowExceptionWhenRequestHasNoChanges() {
        // Given
        Long id = 1L;
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Author Name");
        authorRequest.setBiography("Author Biography");

        Author author = new Author();
        author.setName("Author Name");
        author.setBiography("Author Biography");

        when(authorRepository.findById(id)).thenReturn(Optional.of(author));
        when(authorRepository.save(any())).thenReturn(author);
        // When & Then
        assertThatThrownBy(() -> underTest.updateAuthor(id, authorRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No changes detected");
    }

    @Test
    public void deleteAuthorShouldDeleteAuthorWhenIdExists() {
        // Given
        Long id = 1L;

        when(authorRepository.existsById(id)).thenReturn(true);

        // When
        underTest.deleteAuthor(id);

        // Then
        verify(authorRepository).deleteById(id);
    }

    @Test
    public void deleteAuthorShouldThrowExceptionWhenIdDoesNotExist() {
        // Given
        Long id = 1L;

        when(authorRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy( () -> underTest.deleteAuthor(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author with id [1] not found");
    }
    @Test
    public void getBooksByAuthorShouldReturnPageOfBooksWhenIdExists() {
        // Given
        Book book = new Book();
        book.setTitle("title");
        book.setDescription("description");
        book.setQuantity(1);
        book.setGenre("genre");

        Author author = new Author();
        author.setId(1L);
        author.setName("author");
        author.setBiography("biography");
        book.setAuthor(author);

        BookResponse bookResponse = bookMapper.toDto(book);

        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(authorRepository.existsById(author.getId())).thenReturn(true);
        when(bookRepository.findByAuthorId(author.getId(), PageRequest.of(0, 1))).thenReturn(bookPage);

        // When
        Page<BookResponse> bookResponsePage = underTest.getBooksByAuthor(author.getId(),PageRequest.of(0, 1));

        // Then
        assertThat(bookResponsePage.getContent()).containsExactly(bookResponse);
    }

    @Test
    public void getBooksByAuthorShouldThrowExceptionWhenIdDoesNotExist() {
        // Given
        Long id = 1L;

        when(authorRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy( () -> underTest.getBooksByAuthor(id, PageRequest.of(0, 1)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author with id [1] not found");
    }
}
