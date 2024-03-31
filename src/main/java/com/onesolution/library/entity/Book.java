package com.onesolution.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "title" ,nullable = false ,unique = true)
    private String title;
    @Column(name = "description" ,nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "author_id" ,nullable = false)
    private Author author;
    @Column(name = "quantity" ,nullable = false)
    private int quantity;
    @Column(name = "available_quantity" ,nullable = false)
    private int availableQuantity;
    @Column(name = "genre" ,nullable = false)
    private String genre;
    @Column(name = "published_date" ,nullable = false)
    private LocalDateTime publishedDate;
}
