package com.onesolution.library.controller;

import com.onesolution.library.dto.BookTransactionRequest;
import com.onesolution.library.dto.BorrowedBookResponse;
import com.onesolution.library.service.BookTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookTransactionController {
    private final BookTransactionService bookService;
    @PostMapping("/{id}/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    public void borrowBook(@PathVariable Long id, @RequestBody BookTransactionRequest bookTransactionRequest) {
        bookService.borrowBook(id, bookTransactionRequest);
    }

    @PostMapping("/{id}/return")
    public void returnBook(@PathVariable Long id, @RequestBody BookTransactionRequest bookTransactionRequest) {
        bookService.returnBook(id, bookTransactionRequest);
    }

    @GetMapping("/borrowed")
    public Page<BorrowedBookResponse> getAllBorrowedBooks(Pageable page) {
        return bookService.getAllBorrowedBooks(page);
    }

    @GetMapping("/statistics")
    public Map<String, Object> getBookBorrowingStatistics() {
        return bookService.getStatistics();
    }
}
