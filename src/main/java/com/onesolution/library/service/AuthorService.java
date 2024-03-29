package com.onesolution.library.service;

import com.onesolution.library.dto.AuthorRequest;
import com.onesolution.library.mapper.AuthorMapper;
import com.onesolution.library.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public void createAuthor(AuthorRequest authorRequest) {
        authorRepository.save(authorMapper.toEntity(authorRequest));
    }
}
