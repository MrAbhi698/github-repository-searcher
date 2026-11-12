
package com.freightfox.github_repository_searcher.controller;

import com.freightfox.github_repository_searcher.entity.GithubRepository;
import com.freightfox.github_repository_searcher.service.GithubSearchService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GithubRepositoryController {

    private final GithubSearchService githubSearchService;

    public GithubRepositoryController(
            GithubSearchService githubSearchService) {
        this.githubSearchService = githubSearchService;
    }

    @GetMapping("/repositories")
    public ResponseEntity<List<GithubRepository>> getRepositories(

            @RequestParam(required = false) String language,

            @RequestParam(required = false) Integer minStars,

            @RequestParam(required = false, defaultValue = "stars")
            String sort) {

        List<GithubRepository> repositories =
                githubSearchService.getStoredRepositories(
                        language,
                        minStars,
                        sort
                );

        return ResponseEntity.ok(repositories);
    }
}
