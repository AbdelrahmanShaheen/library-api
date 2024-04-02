package com.onesolution.library.service;

import com.onesolution.library.dto.AuthorRequest;
import com.onesolution.library.dto.AuthorResponse;
import com.onesolution.library.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {
    public void createAuthor(AuthorRequest authorRequest);

    public Page<AuthorResponse> getAllAuthors(Pageable page);

    public AuthorResponse getAuthorById(Long id);

    public void updateAuthor(Long id, AuthorRequest authorRequest);

    public void deleteAuthor(Long id);

    public Page<BookResponse> getBooksByAuthor(Long id, Pageable page);
}
