package com.onesolution.library.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AuthorRequest {
    @NotEmpty(message = "Name is required")
    private String name;
    @NotEmpty(message = "Biography is required")
    private String biography;
}
