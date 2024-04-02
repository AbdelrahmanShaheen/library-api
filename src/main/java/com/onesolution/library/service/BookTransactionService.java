package com.onesolution.library.service;

import com.onesolution.library.dto.BookTransactionRequest;
import com.onesolution.library.dto.BorrowedBookResponse;
import com.onesolution.library.entity.Book;
import com.onesolution.library.entity.BookTransaction;
import com.onesolution.library.entity.Status;
import com.onesolution.library.exception.ConflictException;
import com.onesolution.library.exception.ResourceNotFoundException;
import com.onesolution.library.repository.BookRepository;
import com.onesolution.library.repository.BookTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BookTransactionService {
    public void borrowBook(Long bookId, BookTransactionRequest bookTransactionRequest);

    public void returnBook(Long id, BookTransactionRequest bookTransactionRequest);

    public Page<BorrowedBookResponse> getAllBorrowedBooks(Pageable page);

    public Map<String, Object> getStatistics();
}
