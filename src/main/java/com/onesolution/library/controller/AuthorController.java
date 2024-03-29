package com.onesolution.library.controller;

import com.onesolution.library.dto.AuthorRequest;
import com.onesolution.library.dto.AuthorResponse;
import com.onesolution.library.service.AuthorService;
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
    public void createAuthor(@RequestBody AuthorRequest authorRequest) {
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
    //PUT /authors/{id} - Update author by id

    //DELETE /authors/{id} - Delete author by id

}
