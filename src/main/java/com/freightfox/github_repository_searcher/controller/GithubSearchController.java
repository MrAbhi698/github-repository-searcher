
package com.freightfox.github_repository_searcher.controller;

import com.freightfox.github_repository_searcher.dto.GithubSearchRequest;
import com.freightfox.github_repository_searcher.dto.GithubSearchResponse;
import com.freightfox.github_repository_searcher.entity.GithubRepository;
import com.freightfox.github_repository_searcher.service.GithubSearchService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GithubSearchController {

    private final GithubSearchService githubSearchService;

    public GithubSearchController(
            GithubSearchService githubSearchService) {
        this.githubSearchService = githubSearchService;
    }

    @PostMapping("/search")
    public ResponseEntity<GithubSearchResponse> searchRepositories(
            @Valid @RequestBody GithubSearchRequest request) {

        List<GithubRepository> repositories =
                githubSearchService.searchAndSaveRepositories(request);

        GithubSearchResponse response = new GithubSearchResponse(
                repositories.isEmpty()
                        ? "No repositories found"
                        : "Repositories fetched and saved successfully",
                repositories
        );

        return ResponseEntity.ok(response);
    }
}
