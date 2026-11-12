
package com.freightfox.github_repository_searcher.dto;

import com.freightfox.github_repository_searcher.entity.GithubRepository;

import java.util.List;

public record GithubSearchResponse(
        String message,
        List<GithubRepository> repositories
) {
}
