package com.onesolution.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BorrowedBookResponse {
    private String title;
    private String description;
    private String genre;
}
