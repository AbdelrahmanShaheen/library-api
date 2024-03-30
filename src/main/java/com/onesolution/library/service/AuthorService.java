package com.onesolution.library.service;

import com.onesolution.library.dto.AuthorRequest;
import com.onesolution.library.dto.AuthorResponse;
import com.onesolution.library.dto.BookResponse;
import com.onesolution.library.exception.RequestValidationException;
import com.onesolution.library.exception.ResourceNotFoundException;
import com.onesolution.library.mapper.AuthorMapper;
import com.onesolution.library.mapper.BookMapper;
import com.onesolution.library.repository.AuthorRepository;
import com.onesolution.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final AuthorMapper authorMapper;
    private final BookMapper bookMapper;

    public void createAuthor(AuthorRequest authorRequest) {
        authorRepository.save(authorMapper.toEntity(authorRequest));
    }

    public Page<AuthorResponse> getAllAuthors(Pageable page) {
        return authorRepository.findAll(page).map(authorMapper::toResponse);
    }

    public AuthorResponse getAuthorById(Long id) {
        return authorRepository.findById(id)
                .map(authorMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id [%s] not found".formatted(id)));
    }

    public void updateAuthor(Long id, AuthorRequest authorRequest) {
        final boolean[] changes = {false};
        String name = authorRequest.getName();
        String biography = authorRequest.getBiography();
        authorRepository.findById(id)
                .map(author -> {
                    if (name != null && !name.equals(author.getName()))
                    {
                        author.setName(name);
                        changes[0] = true;
                    }
                    if (biography != null && !biography.equals(author.getBiography()))
                    {
                        author.setBiography(biography);
                        changes[0] = true;
                    }
                    return authorRepository.save(author);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Author with id [%s] not found".formatted(id)));
        if (!changes[0])
        {
            throw new RequestValidationException("No changes detected");
        }
    }

    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id))
        {
            throw new ResourceNotFoundException("Author with id [%s] not found".formatted(id));
        }
        authorRepository.deleteById(id);
    }

    public Page<BookResponse> getBooksByAuthor(Long id, Pageable page) {
        if (!authorRepository.existsById(id))
        {
            throw new ResourceNotFoundException("Author with id [%s] not found".formatted(id));
        }
        return bookRepository.findByAuthorId(id, page).map(bookMapper::toDto);
    }
}
