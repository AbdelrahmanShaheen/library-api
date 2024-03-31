package com.onesolution.library.repository;

import com.onesolution.library.entity.Book;
import com.onesolution.library.entity.BookTransaction;
import com.onesolution.library.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookTransactionRepository extends JpaRepository<BookTransaction, Long> {
    boolean existsByBookIdAndBorrowerEmailAndStatus(Long bookId, String email, Status status);
    Optional<BookTransaction> findByBookIdAndBorrowerEmailAndStatus(Long bookId, String email, Status status);
}
