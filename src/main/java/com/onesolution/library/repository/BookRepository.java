package com.onesolution.library.repository;

import com.onesolution.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long>{
    boolean existsByTitle(String title);
}
