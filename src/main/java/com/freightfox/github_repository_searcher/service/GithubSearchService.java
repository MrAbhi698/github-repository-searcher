
package com.freightfox.github_repository_searcher.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freightfox.github_repository_searcher.client.GithubApiClient;
import com.freightfox.github_repository_searcher.dto.GithubApiResponse;
import com.freightfox.github_repository_searcher.dto.GithubSearchRequest;
import com.freightfox.github_repository_searcher.entity.GithubRepository;
import com.freightfox.github_repository_searcher.repository.GithubRepositoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GithubSearchService {

    private final GithubApiClient githubApiClient;
    private final GithubRepositoryRepository repository;
    private final ObjectMapper objectMapper;

    public GithubSearchService(
            GithubApiClient githubApiClient,
            GithubRepositoryRepository repository,
            ObjectMapper objectMapper) {

        this.githubApiClient = githubApiClient;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public List<GithubRepository> searchAndSaveRepositories(
            GithubSearchRequest request) {

        // Set default sorting
        String sort = (request.sort() == null ||
                request.sort().isBlank())
                ? "stars"
                : request.sort().toLowerCase();

        // Call GitHub API
        String jsonResponse = githubApiClient.searchRepositories(
                request.query(),
                request.language(),
                sort
        );

        if (jsonResponse == null || jsonResponse.isBlank()) {
            throw new IllegalStateException(
                    "GitHub returned an empty response"
            );
        }

        // Convert JSON response into DTO
        GithubApiResponse apiResponse;

        try {
            apiResponse = objectMapper.readValue(
                    jsonResponse,
                    GithubApiResponse.class
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Unable to process GitHub API response",
                    exception
            );
        }

        if (apiResponse.items() == null ||
                apiResponse.items().isEmpty()) {
            return List.of();
        }

        List<GithubRepository> savedRepositories =
                new ArrayList<>();

        // Process each GitHub repository
        for (GithubApiResponse.GithubRepositoryDto dto
                : apiResponse.items()) {

            if (dto.id() == null ||
                    dto.name() == null ||
                    dto.owner() == null ||
                    dto.owner().login() == null) {
                continue;
            }

            // Check whether repository already exists
            GithubRepository entity = repository
                    .findById(dto.id())
                    .orElseGet(GithubRepository::new);

            // Update or populate repository details
            entity.setId(dto.id());
            entity.setName(dto.name());
            entity.setDescription(dto.description());
            entity.setOwner(dto.owner().login());
            entity.setLanguage(dto.language());
            entity.setStars(
                    dto.stars() != null ? dto.stars() : 0
            );
            entity.setForks(
                    dto.forks() != null ? dto.forks() : 0
            );
            entity.setLastUpdated(dto.lastUpdated());

            savedRepositories.add(entity);
        }

        // Save new repositories and update existing ones
        return repository.saveAll(savedRepositories);
    }


    @Transactional(readOnly = true)
    public List<GithubRepository> getStoredRepositories(
            String language, Integer minStars, String sortBy) {

        String sortField = (sortBy == null || sortBy.isBlank())
                ? "stars"
                : sortBy.toLowerCase().trim();

        switch (sortField) {
            case "stars" -> sortField = "stars";
            case "forks" -> sortField = "forks";
            case "updated" -> sortField = "lastUpdated";
            default -> throw new IllegalArgumentException(
                    "Invalid sort value. Allowed values: stars, forks, updated");
        }

        if (language != null && language.isBlank()) {
            language = null;
        }

        if (minStars != null && minStars < 0) {
            throw new IllegalArgumentException(
                    "Minimum stars cannot be negative");
        }

        Sort sorting = Sort.by(Sort.Direction.DESC, sortField);

        if (language != null && minStars != null) {
            return repository.findByLanguageIgnoreCaseAndStarsGreaterThanEqual(
                    language, minStars, sorting);
        }

        if (language != null) {
            return repository.findByLanguageIgnoreCase(language, sorting);
        }

        if (minStars != null) {
            return repository.findByStarsGreaterThanEqual(minStars, sorting);
        }

        return repository.findAll(sorting);
    }

}