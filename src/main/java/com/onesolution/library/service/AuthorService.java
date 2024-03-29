package com.onesolution.library.service;

import com.onesolution.library.dto.AuthorRequest;
import com.onesolution.library.dto.AuthorResponse;
import com.onesolution.library.exception.ResourceNotFoundException;
import com.onesolution.library.mapper.AuthorMapper;
import com.onesolution.library.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public void createAuthor(AuthorRequest authorRequest) {
        authorRepository.save(authorMapper.toEntity(authorRequest));
    }

    public Page<AuthorResponse> getAllAuthors(Pageable page) {
        return authorRepository.findAll(page).map(authorMapper::toResponse);
    }

    public AuthorResponse getAuthorById(Long id) {
        return authorRepository.findById(id)
                .map(authorMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
    }

    public void updateAuthor(Long id, AuthorRequest authorRequest) {
        authorRepository.findById(id)
                .map(author -> {
                    if (authorRequest.getName() != null)
                    {
                        author.setName(authorRequest.getName());
                    }
                    if (authorRequest.getBiography() != null)
                    {
                        author.setBiography(authorRequest.getBiography());
                    }
                    return authorRepository.save(author);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }
}
