package com.onesolution.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookResponse {
    private String title;
    private String description;
    private AuthorResponse author;
    private int quantity;
    private int availableQuantity;
    private String genre;
}
