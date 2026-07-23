package com.woodcompany.dto.quote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateQuoteStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "(?i)pending|approved|rejected",
            message = "Status must be pending, approved or rejected")
    private String status;
}