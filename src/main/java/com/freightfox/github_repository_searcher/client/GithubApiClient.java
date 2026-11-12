
package com.freightfox.github_repository_searcher.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GithubApiClient {

    private final RestClient restClient;

    public GithubApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(
                        HttpHeaders.ACCEPT,
                        "application/vnd.github+json"
                )
                .defaultHeader(
                        HttpHeaders.USER_AGENT,
                        "FreightFox-GitHub-Repository-Searcher"
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    public String searchRepositories(String query,
                                     String language,
                                     String sort) {

        StringBuilder searchQuery = new StringBuilder(query);

        if (language != null && !language.isBlank()) {
            searchQuery.append(" language:")
                    .append(language.trim());
        }

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParam("q", searchQuery.toString())
                        .queryParam("sort", sort)
                        .queryParam("order", "desc")
                        .queryParam("per_page", 30)
                        .build())
                .retrieve()
                .body(String.class);
    }
}
