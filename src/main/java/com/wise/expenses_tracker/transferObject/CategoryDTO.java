package com.wise.expenses_tracker.transferObject;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Category")
public class CategoryDTO {

    @Schema(description = "Unique identifier for the category")
    @JsonProperty("category_id")
    private Long id;

    @Schema(description = "Name of the category")
    @NotBlank(message = "Category name cannot be blank")
    @JsonProperty("category_name")
    private String name;
}
