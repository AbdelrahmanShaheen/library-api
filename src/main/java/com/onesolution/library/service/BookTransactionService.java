package com.onesolution.library.service;

import com.onesolution.library.dto.BookTransactionRequest;
import com.onesolution.library.dto.BorrowedBookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface BookTransactionService {
    public void borrowBook(Long bookId, BookTransactionRequest bookTransactionRequest);

    public void returnBook(Long id, BookTransactionRequest bookTransactionRequest);

    public Page<BorrowedBookResponse> getAllBorrowedBooks(Pageable page);

    public Map<String, Object> getStatistics();
}
