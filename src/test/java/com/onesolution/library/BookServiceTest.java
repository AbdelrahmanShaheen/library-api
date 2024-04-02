package com.onesolution.library;

import com.onesolution.library.dto.BookRequest;
import com.onesolution.library.dto.BookResponse;
import com.onesolution.library.entity.Author;
import com.onesolution.library.entity.Book;
import com.onesolution.library.exception.ConflictException;
import com.onesolution.library.exception.RequestValidationException;
import com.onesolution.library.exception.ResourceNotFoundException;
import com.onesolution.library.mapper.BookMapper;
import com.onesolution.library.mapper.Impl.BookMapperImpl;
import com.onesolution.library.repository.AuthorRepository;
import com.onesolution.library.repository.BookRepository;
import com.onesolution.library.service.BookService;

import com.onesolution.library.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class BookServiceTest {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    private final BookService underTest;

    public BookServiceTest() {
        bookRepository = mock(BookRepository.class);
        authorRepository = mock(AuthorRepository.class);
        bookMapper = new BookMapperImpl();
        underTest = new BookServiceImpl(bookRepository, authorRepository, bookMapper);
    }
    @Test
    public void addBookShouldSuccess() {
        // Given
        BookRequest bookRequest = new BookRequest("title", "description", 1L, 1, "genre");
        Author author = Author.builder()
                .id(1L)
                .name("author")
                .biography("biography").build();

        when(bookRepository.existsByTitle(bookRequest.getTitle())).thenReturn(false);
        when(authorRepository.findById(bookRequest.getAuthorId())).thenReturn(Optional.of(author));

        // When
        underTest.addBook(bookRequest);

        // Then
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookArgumentCaptor.capture());
        Book capturedBook = bookArgumentCaptor.getValue();

        assertThat(capturedBook.getId()).isNull();
        assertThat(capturedBook.getTitle()).isEqualTo(bookRequest.getTitle());
        assertThat(capturedBook.getDescription()).isEqualTo(bookRequest.getDescription());
        assertThat(capturedBook.getQuantity()).isEqualTo(bookRequest.getQuantity());
        assertThat(capturedBook.getGenre()).isEqualTo(bookRequest.getGenre());
        assertThat(capturedBook.getAuthor()).isEqualTo(author);
    }
    @Test
    public void addBookShouldThrowConflictExceptionWhenBookIsPresent() {
        // Given
        BookRequest bookRequest = new BookRequest("title", "description", 1L, 1, "genre");

        when(bookRepository.existsByTitle(bookRequest.getTitle())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> underTest.addBook(bookRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Book with title " + bookRequest.getTitle() + " already exists");
    }
    @Test
    void addBookShouldThrowResourceNotFoundExceptionWhenAuthorNotFound() {
        // Given
        BookRequest bookRequest = new BookRequest("title", "description", 1L, 1, "genre");

        when(bookRepository.existsByTitle(bookRequest.getTitle())).thenReturn(false);
        when(authorRepository.findById(bookRequest.getAuthorId())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> underTest.addBook(bookRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author with id [%s] not found".formatted(bookRequest.getAuthorId()));
    }
    @Test
    void getAllBooksShouldReturnPageOfBookResponse() {
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
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);

        // When
        Page<BookResponse> bookResponsePage = underTest.getAllBooks(PageRequest.of(0, 5));

        // Then
        assertThat(bookResponsePage.getContent()).containsExactly(bookResponse);
    }
    @Test
    void getBookByIdShouldReturnBookResponse() {
        // Given
        Long id = 1L;
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

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // When
        BookResponse result = underTest.getBookById(id);

        // Then
        verify(bookRepository).findById(id);
        assertThat(result.getTitle()).isEqualTo(bookResponse.getTitle());
        assertThat(result.getDescription()).isEqualTo(bookResponse.getDescription());
        assertThat(result.getQuantity()).isEqualTo(bookResponse.getQuantity());
        assertThat(result.getGenre()).isEqualTo(bookResponse.getGenre());
        assertThat(result.getAuthor()).isEqualTo(bookResponse.getAuthor());
    }
    @Test
    void getBookByIdShouldThrowResourceNotFoundExceptionWhenBookDoesNotExist() {
        // Given
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.getBookById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book with id [%s] does not exist".formatted(id));
    }
    @Test
    void updateBookShouldUpdateBookDetails() {
        // Given
        Long id = 1L;
        BookRequest bookRequest = new BookRequest("new title", "new description", 2L, 2, "new genre");

        Book book = new Book();
        book.setTitle("old title");
        book.setDescription("old description");
        book.setQuantity(1);
        book.setGenre("old genre");

        Author oldAuthor = new Author();
        oldAuthor.setId(1L);
        oldAuthor.setName("old author");
        oldAuthor.setBiography("old biography");
        book.setAuthor(oldAuthor);

        Author newAuthor = new Author();
        newAuthor.setId(2L);
        newAuthor.setName("new author");
        newAuthor.setBiography("new biography");

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.existsByTitle(bookRequest.getTitle())).thenReturn(false);
        when(authorRepository.findById(bookRequest.getAuthorId())).thenReturn(Optional.of(newAuthor));

        // When
        underTest.updateBook(id, bookRequest);

        // Then
        verify(bookRepository).save(any(Book.class));
    }
    @Test
    void updateBookShouldThrowConflictExceptionWhenTitleExists() {
        // Given
        Long id = 1L;
        BookRequest bookRequest = new BookRequest("new title", "new description", 2L, 2, "new genre");

        Book book = new Book();
        book.setTitle("old title");
        book.setDescription("old description");
        book.setQuantity(1);
        book.setGenre("old genre");

        Author oldAuthor = new Author();
        oldAuthor.setId(1L);
        oldAuthor.setName("old author");
        oldAuthor.setBiography("old biography");
        book.setAuthor(oldAuthor);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.existsByTitle(bookRequest.getTitle())).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> underTest.updateBook(id, bookRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Book with title: " + bookRequest.getTitle() + ". already exists");
    }

    @Test
    void updateBookShouldThrowResourceNotFoundExceptionWhenAuthorNotFound() {
        // Given
        Long id = 1L;
        BookRequest bookRequest = new BookRequest("new title", "new description", 2L, 2, "new genre");

        Book book = new Book();
        book.setTitle("old title");
        book.setDescription("old description");
        book.setQuantity(1);
        book.setGenre("old genre");

        Author oldAuthor = new Author();
        oldAuthor.setId(1L);
        oldAuthor.setName("old author");
        oldAuthor.setBiography("old biography");
        book.setAuthor(oldAuthor);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.existsByTitle(bookRequest.getTitle())).thenReturn(false);
        when(authorRepository.findById(bookRequest.getAuthorId())).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.updateBook(id, bookRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author with id [%s] does not exist".formatted(bookRequest.getAuthorId()));
    }

    @Test
    void updateBookShouldThrowRequestValidationExceptionWhenNoChangesDetected() {
        // Given
        Long id = 1L;
        BookRequest bookRequest = new BookRequest("old title", "old description", 1L, 1, "old genre");

        Book book = new Book();
        book.setTitle("old title");
        book.setDescription("old description");
        book.setQuantity(1);
        book.setGenre("old genre");

        Author oldAuthor = new Author();
        oldAuthor.setId(1L);
        oldAuthor.setName("old author");
        oldAuthor.setBiography("old biography");
        book.setAuthor(oldAuthor);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // When / Then
        assertThatThrownBy(() -> underTest.updateBook(id, bookRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No changes detected");
    }
    @Test
    void updateBookShouldThrowRequestValidationExceptionWhenQuantityIsNegative() {
        // Given
        Long id = 1L;
        BookRequest bookRequest = new BookRequest("new title", "new description", 2L, -1, "new genre");

        Book book = new Book();
        book.setTitle("old title");
        book.setDescription("old description");
        book.setQuantity(1);
        book.setGenre("old genre");

        Author oldAuthor = new Author();
        oldAuthor.setId(1L);
        oldAuthor.setName("old author");
        oldAuthor.setBiography("old biography");
        book.setAuthor(oldAuthor);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // When / Then
        assertThatThrownBy(() -> underTest.updateBook(id, bookRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("Quantity cannot be negative");
    }
    @Test
    void deleteBookShouldDeleteBook() {
        // Given
        Long id = 1L;
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

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // When
        underTest.deleteBook(id);

        // Then
        verify(bookRepository).delete(book);
    }
    @Test
    void deleteBookShouldThrowResourceNotFoundExceptionWhenBookDoesNotExist() {
        // Given
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.deleteBook(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book with id [%s] does not exist".formatted(id));
    }
    @Test
    void searchBooksByShouldReturnBooksByTitle() {
        // Given
        String title = "title";
        Pageable pageable = PageRequest.of(0, 5);

        Book book = new Book();
        book.setTitle(title);
        book.setDescription("description");
        book.setQuantity(1);
        book.setGenre("genre");

        Author author = new Author();
        author.setId(1L);
        author.setName("author");
        author.setBiography("biography");
        book.setAuthor(author);

        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.findByTitle(title, pageable)).thenReturn(bookPage);

        // When
        Page<BookResponse> result = underTest.searchBooksBy(title, null, pageable);

        // Then
        assertThat(result.getContent().get(0).getTitle()).isEqualTo(title);
    }

    @Test
    void searchBooksByShouldReturnBooksByGenre() {
        // Given
        String genre = "genre";
        Pageable pageable = PageRequest.of(0, 5);

        Book book = new Book();
        book.setTitle("title");
        book.setDescription("description");
        book.setQuantity(1);
        book.setGenre(genre);

        Author author = new Author();
        author.setId(1L);
        author.setName("author");
        author.setBiography("biography");
        book.setAuthor(author);

        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.findByGenre(genre, pageable)).thenReturn(bookPage);

        // When
        Page<BookResponse> result = underTest.searchBooksBy(null, genre, pageable);

        // Then
        assertThat(result.getContent().get(0).getGenre()).isEqualTo(genre);
    }

    @Test
    void searchBooksByShouldReturnAllBooksWhenNeitherTitleNorGenreIsProvided() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);

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

        Page<Book> bookPage = new PageImpl<>(List.of(book));

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);

        // When
        Page<BookResponse> result = underTest.searchBooksBy(null, null, pageable);

        // Then
        assertThat(result.getContent().get(0)).isEqualTo(bookMapper.toDto(book));
    }
}
