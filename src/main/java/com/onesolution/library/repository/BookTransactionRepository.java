package com.onesolution.library.repository;

import com.onesolution.library.entity.BookTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookTransactionRepository extends JpaRepository<BookTransaction, Long> {
}
