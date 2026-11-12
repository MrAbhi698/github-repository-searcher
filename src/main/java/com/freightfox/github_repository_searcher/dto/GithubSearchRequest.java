
package com.freightfox.github_repository_searcher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GithubSearchRequest(

        @NotBlank(message = "Search query cannot be empty")
        @Size(max = 256, message = "Search query cannot exceed 256 characters")
        String query,

        @Size(max = 100, message = "Language cannot exceed 100 characters")
        String language,

        @Pattern(
                regexp = "(?i)stars|forks|updated",
                message = "Sort must be stars, forks, or updated"
        )
        String sort

) {
}
