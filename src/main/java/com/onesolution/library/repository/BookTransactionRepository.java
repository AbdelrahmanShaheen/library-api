package com.onesolution.library.repository;

import com.onesolution.library.entity.Book;
import com.onesolution.library.entity.BookTransaction;
import com.onesolution.library.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookTransactionRepository extends JpaRepository<BookTransaction, Long> {
    boolean existsByBookIdAndBorrowerEmailAndStatus(Long bookId, String email, Status status);

}
