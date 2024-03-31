package com.onesolution.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookTransactionRequest {
    @NotEmpty(message = "Borrower email is required")
    @Email(message = "Invalid email format")
    private String borrowerEmail;
}
