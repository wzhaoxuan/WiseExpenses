package com.wise.expenses_tracker.transferObject;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Expenses")
public class ExpensesDTO {

    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Instant date;

    @NotNull
    @JsonProperty("payBy")
    private String pay_by;

    @NotNull
    private Double amount;

    private String description;

    private CategoryDTO category;

}
