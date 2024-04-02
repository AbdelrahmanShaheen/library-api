package com.onesolution.library;

import com.onesolution.library.dto.BookTransactionRequest;
import com.onesolution.library.dto.BorrowedBookResponse;
import com.onesolution.library.entity.Author;
import com.onesolution.library.entity.Book;
import com.onesolution.library.entity.BookTransaction;
import com.onesolution.library.entity.Status;
import com.onesolution.library.exception.ConflictException;
import com.onesolution.library.exception.ResourceNotFoundException;
import com.onesolution.library.repository.BookRepository;
import com.onesolution.library.repository.BookTransactionRepository;
import com.onesolution.library.service.BookTransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookTransactionServiceTest {
    private final BookTransactionRepository bookTransactionRepository;
    private final BookRepository bookRepository;
    private final BookTransactionService underTest;
    public BookTransactionServiceTest(){
        bookTransactionRepository = mock(BookTransactionRepository.class);
        bookRepository = mock(BookRepository.class);
        underTest = new BookTransactionService(bookTransactionRepository, bookRepository);
    }
    @Test
    void borrowBookShouldSuccess() {
        // Given
        Long bookId = 1L;
        BookTransactionRequest bookTransactionRequest = new BookTransactionRequest("user@example.com");

        Book book = new Book();
        book.setTitle("title");
        book.setDescription("description");
        book.setQuantity(1);
        book.setAvailableQuantity(1);
        book.setGenre("genre");

        Author author = new Author();
        author.setId(1L);
        author.setName("author");
        author.setBiography("biography");
        book.setAuthor(author);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookTransactionRepository.existsByBookIdAndBorrowerEmailAndStatus(bookId, bookTransactionRequest.getBorrowerEmail(), Status.BORROWED)).thenReturn(false);

        // When
        underTest.borrowBook(bookId, bookTransactionRequest);

        // Then
        verify(bookTransactionRepository).save(any(BookTransaction.class));
        assertThat(book.getAvailableQuantity()).isEqualTo(0);
    }

    @Test
    void borrowBookShouldThrowResourceNotFoundExceptionWhenBookIsNotAvailable() {
        // Given
        Long bookId = 1L;
        BookTransactionRequest bookTransactionRequest = new BookTransactionRequest("user@example.com");

        Book book = new Book();
        book.setTitle("title");
        book.setDescription("description");
        book.setQuantity(0);
        book.setGenre("genre");

        Author author = new Author();
        author.setId(1L);
        author.setName("author");
        author.setBiography("biography");
        book.setAuthor(author);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When / Then
        assertThatThrownBy(() -> underTest.borrowBook(bookId, bookTransactionRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book with id [%s] is not available".formatted(bookId));
    }

    @Test
    void borrowBookShouldThrowConflictExceptionWhenUserHasAlreadyBorrowedBookAndNotReturnedItYet() {
        // Given
        Long bookId = 1L;
        BookTransactionRequest bookTransactionRequest = new BookTransactionRequest("user@example.com");

        Book book = new Book();
        book.setTitle("title");
        book.setDescription("description");
        book.setQuantity(1);
        book.setAvailableQuantity(1);
        book.setGenre("genre");

        Author author = new Author();
        author.setId(1L);
        author.setName("author");
        author.setBiography("biography");
        book.setAuthor(author);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookTransactionRepository.existsByBookIdAndBorrowerEmailAndStatus(bookId, bookTransactionRequest.getBorrowerEmail(), Status.BORROWED)).thenReturn(true);

        // When / Then
        assertThatThrownBy(() -> underTest.borrowBook(bookId, bookTransactionRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("The same person is trying to borrow a book he borrowed and not returned it yet");
    }

    @Test
    void borrowBookShouldThrowResourceNotFoundExceptionWhenBookDoesNotExist() {
        // Given
        Long bookId = 1L;
        BookTransactionRequest bookTransactionRequest = new BookTransactionRequest("user@example.com");

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.borrowBook(bookId, bookTransactionRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Book with id [%s] does not exist".formatted(bookId));
    }
    @Test
    void returnBookShouldReturnBookWhenBookIsBorrowedByUser() {
        // Given
        Long bookId = 1L;
        BookTransactionRequest bookTransactionRequest = new BookTransactionRequest("user@example.com");

        Book book = new Book();
        book.setTitle("title");
        book.setDescription("description");
        book.setQuantity(1);
        book.setAvailableQuantity(0);
        book.setGenre("genre");

        Author author = new Author();
        author.setId(1L);
        author.setName("author");
        author.setBiography("biography");
        book.setAuthor(author);

        BookTransaction bookTransaction = new BookTransaction();
        bookTransaction.setBook(book);
        bookTransaction.setBorrowerEmail(bookTransactionRequest.getBorrowerEmail());
        bookTransaction.setBorrowedDate(LocalDateTime.now());
        bookTransaction.setDueDate(LocalDateTime.now().plusWeeks(2));
        bookTransaction.setStatus(Status.BORROWED);

        when(bookTransactionRepository.findByBookIdAndBorrowerEmailAndStatus(bookId, bookTransactionRequest.getBorrowerEmail(), Status.BORROWED)).thenReturn(Optional.of(bookTransaction));

        // When
        underTest.returnBook(bookId, bookTransactionRequest);

        // Then
        verify(bookTransactionRepository).save(any(BookTransaction.class));
        assertThat(book.getAvailableQuantity()).isEqualTo(1);
    }

    @Test
    void returnBookShouldThrowResourceNotFoundExceptionWhenBookIsNotBorrowedByUserOrNoBookFound() {
        // Given
        Long bookId = 1L;
        BookTransactionRequest bookTransactionRequest = new BookTransactionRequest("user@example.com");

        when(bookTransactionRepository.findByBookIdAndBorrowerEmailAndStatus(bookId, bookTransactionRequest.getBorrowerEmail(), Status.BORROWED)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> underTest.returnBook(bookId, bookTransactionRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No borrowed book found for the given id and borrower email");
    }
    @Test
    void getAllBorrowedBooksShouldReturnAllBorrowedBooks() {
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

        BookTransaction bookTransaction = new BookTransaction();
        bookTransaction.setBook(book);
        bookTransaction.setBorrowerEmail("user@example.com");
        bookTransaction.setBorrowedDate(LocalDateTime.now());
        bookTransaction.setDueDate(LocalDateTime.now().plusWeeks(2));
        bookTransaction.setStatus(Status.BORROWED);

        Page<BookTransaction> bookTransactionPage = new PageImpl<>(List.of(bookTransaction));

        when(bookTransactionRepository.findByStatus(Status.BORROWED, pageable)).thenReturn(bookTransactionPage);

        // When
        Page<BorrowedBookResponse> result = underTest.getAllBorrowedBooks(pageable);

        // Then
        assertThat(result.getContent().get(0).getTitle()).isEqualTo(book.getTitle());
        assertThat(result.getContent().get(0).getDescription()).isEqualTo(book.getDescription());
        assertThat(result.getContent().get(0).getGenre()).isEqualTo(book.getGenre());
    }
    @Test
    void getStatisticsShouldReturnCorrectStatistics() {
        // Given
        long totalBorrowedBooks = 5L;

        Book book1 = new Book();
        book1.setTitle("Book 1");
        Book book2 = new Book();
        book2.setTitle("Book 2");

        List<Object[]> mostBorrowedBooks = List.of(new Object[]{book1, 2L}, new Object[]{book2, 3L});
        List<BookTransaction> overdueBooks = List.of(new BookTransaction(), new BookTransaction());

        when(bookTransactionRepository.count()).thenReturn(totalBorrowedBooks);
        when(bookTransactionRepository.findMostBorrowedBooks()).thenReturn(mostBorrowedBooks);
        when(bookTransactionRepository.findOverdueBooks()).thenReturn(overdueBooks);

        // When
        Map<String, Object> result = underTest.getStatistics();

        // Then
        assertThat(result.get("totalBorrowedBooks")).isEqualTo(totalBorrowedBooks);
        assertThat(((Map<String, Long>) result.get("mostBorrowedBooks")).size()).isEqualTo(mostBorrowedBooks.size());
        assertThat(result.get("overdueBooks")).isEqualTo(overdueBooks.size());
    }
}
