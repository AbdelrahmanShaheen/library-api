package com.onesolution.library.controller;

import com.onesolution.library.dto.AuthorRequest;
import com.onesolution.library.dto.AuthorResponse;
import com.onesolution.library.dto.BookResponse;
import com.onesolution.library.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void createAuthor(@RequestBody @Valid AuthorRequest authorRequest) {
        authorService.createAuthor(authorRequest);
    }

    @GetMapping("")
    Page<AuthorResponse> getAllAuthors(Pageable page) {
        return authorService.getAllAuthors(page);
    }

    @GetMapping("/{id}")
    AuthorResponse getAuthorById(@PathVariable Long id) {
        return authorService.getAuthorById(id);
    }

    @PutMapping("/{id}")
    public void updateAuthor(@PathVariable Long id, @RequestBody AuthorRequest authorRequest) {
        authorService.updateAuthor(id, authorRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
    }
    //getting all books related to an author
    @GetMapping("/{id}/books")
    public Page<BookResponse> getBooksByAuthor(@PathVariable Long id, Pageable page) {
        return authorService.getBooksByAuthor(id, page);
    }
}
