package com.onesolution.library.service.impl;

import com.onesolution.library.dto.BookTransactionRequest;
import com.onesolution.library.dto.BorrowedBookResponse;
import com.onesolution.library.entity.Book;
import com.onesolution.library.entity.BookTransaction;
import com.onesolution.library.entity.Status;
import com.onesolution.library.exception.ConflictException;
import com.onesolution.library.exception.ResourceNotFoundException;
import com.onesolution.library.repository.BookRepository;
import com.onesolution.library.repository.BookTransactionRepository;
import com.onesolution.library.service.BookTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookTransactionServiceImpl implements BookTransactionService {
    private final BookTransactionRepository bookTransactionRepository;
    private final BookRepository bookRepository;
    public void borrowBook(Long bookId, BookTransactionRequest bookTransactionRequest) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id [%s] does not exist".formatted(bookId)));

        if (book.getAvailableQuantity() == 0) {
            throw new ResourceNotFoundException("Book with id [%s] is not available".formatted(bookId));
        }

        boolean hasBorrowedBook = bookTransactionRepository.existsByBookIdAndBorrowerEmailAndStatus(bookId, bookTransactionRequest.getBorrowerEmail(), Status.BORROWED);
        if (hasBorrowedBook) {
            throw new ConflictException("The same person is trying to borrow a book he borrowed and not returned it yet");
        }
        // create a new BookTransaction
        BookTransaction bookTransaction = new BookTransaction();
        bookTransaction.setBook(book);
        bookTransaction.setBorrowerEmail(bookTransactionRequest.getBorrowerEmail());
        bookTransaction.setBorrowedDate(LocalDateTime.now());
        bookTransaction.setDueDate(LocalDateTime.now().plusWeeks(2));
        bookTransaction.setStatus(Status.BORROWED);

        bookTransactionRepository.save(bookTransaction);

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);

        bookRepository.save(book);
    }

    public void returnBook(Long id, BookTransactionRequest bookTransactionRequest) {

        BookTransaction bookTransaction = bookTransactionRepository.findByBookIdAndBorrowerEmailAndStatus(id, bookTransactionRequest.getBorrowerEmail(), Status.BORROWED)
                .orElseThrow(() -> new ResourceNotFoundException("No borrowed book found for the given id and borrower email"));

        bookTransaction.setStatus(Status.RETURNED);
        bookTransaction.setReturnedDate(LocalDateTime.now());

        Book book = bookTransaction.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);

        bookTransactionRepository.save(bookTransaction);
        bookRepository.save(book);
    }

    public Page<BorrowedBookResponse> getAllBorrowedBooks(Pageable page) {

        Page<BookTransaction> borrowedBooksPage = bookTransactionRepository.findByStatus(Status.BORROWED, page);

        return borrowedBooksPage.map(bookTransaction -> {
            Book book = bookTransaction.getBook();
            return BorrowedBookResponse.builder()
                    .title(book.getTitle())
                    .description(book.getDescription())
                    .genre(book.getGenre())
                    .build();
        });
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        long totalBorrowedBooks = bookTransactionRepository.count();
        List<Object[]> mostBorrowedBooks = bookTransactionRepository.findMostBorrowedBooks();
        List<BookTransaction>overdueBooks = bookTransactionRepository.findOverdueBooks();

        Map<String, Long> mostBorrowedBooksMap = new HashMap<>();
        for (Object[] objects : mostBorrowedBooks) {
            Book book = (Book) objects[0];
            Long count = (Long) objects[1];
            mostBorrowedBooksMap.put(book.getTitle(), count);
        }
        statistics.put("totalBorrowedBooks", totalBorrowedBooks);
        statistics.put("mostBorrowedBooks", mostBorrowedBooksMap);
        statistics.put("overdueBooks", overdueBooks.size());

        return statistics;
    }
}
