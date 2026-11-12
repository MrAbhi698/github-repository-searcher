
package com.freightfox.github_repository_searcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubApiResponse(
        @JsonProperty("items")
        List<GithubRepositoryDto> items
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GithubRepositoryDto(

            Long id,

            String name,

            String description,

            String language,

            @JsonProperty("stargazers_count")
            Integer stars,

            @JsonProperty("forks_count")
            Integer forks,

            @JsonProperty("updated_at")
            Instant lastUpdated,

            GithubOwnerDto owner
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GithubOwnerDto(
            String login
    ) {
    }
}
