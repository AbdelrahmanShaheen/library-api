package com.onesolution.library.repository;

import com.onesolution.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>{
    boolean existsByTitle(String title);
    Page<Book> findByTitle(String title, Pageable page);
    Page<Book> findByGenre(String genre, Pageable page);
    Page<Book> findByAuthorId(Long id, Pageable page);
}
