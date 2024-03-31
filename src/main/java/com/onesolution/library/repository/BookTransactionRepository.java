package com.onesolution.library.repository;

import com.onesolution.library.entity.Book;
import com.onesolution.library.entity.BookTransaction;
import com.onesolution.library.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookTransactionRepository extends JpaRepository<BookTransaction, Long> {
    boolean existsByBookIdAndBorrowerEmailAndStatus(Long bookId, String email, Status status);
    Optional<BookTransaction> findByBookIdAndBorrowerEmailAndStatus(Long bookId, String email, Status status);
    Page<BookTransaction> findByStatus(Status status, Pageable pageable);
    @Query("SELECT bt.book, COUNT(bt.book) AS count FROM BookTransaction bt GROUP BY bt.book ORDER BY count DESC")
    List<Object[]> findMostBorrowedBooks();
    @Query("SELECT bt FROM BookTransaction bt WHERE bt.status = 'BORROWED' AND bt.dueDate < CURRENT_DATE")
    List<BookTransaction> findOverdueBooks();
}
