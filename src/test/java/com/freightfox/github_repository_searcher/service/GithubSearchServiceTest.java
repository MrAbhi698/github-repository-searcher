
package com.freightfox.github_repository_searcher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freightfox.github_repository_searcher.client.GithubApiClient;
import com.freightfox.github_repository_searcher.dto.GithubApiResponse;
import com.freightfox.github_repository_searcher.dto.GithubSearchRequest;
import com.freightfox.github_repository_searcher.entity.GithubRepository;
import com.freightfox.github_repository_searcher.repository.GithubRepositoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubSearchServiceTest {

    @Mock
    private GithubApiClient githubApiClient;

    @Mock
    private GithubRepositoryRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GithubSearchService githubSearchService;

    @Test
    void shouldReturnAllRepositoriesSortedByStarsByDefault() {

        List<GithubRepository> repositories = List.of(
                new GithubRepository(
                        1L, "spring-boot-demo", "Demo project",
                        "user1", "Java", 500, 100, null
                )
        );

        when(repository.findAll(
                Sort.by(Sort.Direction.DESC, "stars")
        )).thenReturn(repositories);

        List<GithubRepository> result =
                githubSearchService.getStoredRepositories(
                        null, null, null
                );

        assertEquals(1, result.size());
        assertEquals("spring-boot-demo", result.get(0).getName());

        verify(repository).findAll(
                Sort.by(Sort.Direction.DESC, "stars")
        );
    }

    @Test
    void shouldFilterRepositoriesByLanguage() {

        List<GithubRepository> repositories = List.of(
                new GithubRepository(
                        2L, "java-project", "Java project",
                        "user2", "Java", 300, 50, null
                )
        );

        when(repository.findByLanguageIgnoreCase(
                "Java",
                Sort.by(Sort.Direction.DESC, "stars")
        )).thenReturn(repositories);

        List<GithubRepository> result =
                githubSearchService.getStoredRepositories(
                        "Java", null, null
                );

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getLanguage());

        verify(repository).findByLanguageIgnoreCase(
                "Java",
                Sort.by(Sort.Direction.DESC, "stars")
        );
    }


    @Test
    void shouldFilterRepositoriesByMinimumStars() {

        List<GithubRepository> repositories = List.of(
                new GithubRepository(
                        3L, "popular-project", "Popular project",
                        "user3", "Java", 1000, 200, null
                )
        );

        when(repository.findByStarsGreaterThanEqual(
                500,
                Sort.by(Sort.Direction.DESC, "stars")
        )).thenReturn(repositories);

        List<GithubRepository> result =
                githubSearchService.getStoredRepositories(
                        null, 500, null
                );

        assertEquals(1, result.size());
        assertEquals(1000, result.get(0).getStars());

        verify(repository).findByStarsGreaterThanEqual(
                500,
                Sort.by(Sort.Direction.DESC, "stars")
        );
    }

    @Test
    void shouldSortRepositoriesByForks() {

        List<GithubRepository> repositories = List.of(
                new GithubRepository(
                        4L, "forked-project", "Forked project",
                        "user4", "Java", 200, 150, null
                )
        );

        when(repository.findAll(
                Sort.by(Sort.Direction.DESC, "forks")
        )).thenReturn(repositories);

        List<GithubRepository> result =
                githubSearchService.getStoredRepositories(
                        null, null, "forks"
                );

        assertEquals(1, result.size());
        assertEquals(150, result.get(0).getForks());

        verify(repository).findAll(
                Sort.by(Sort.Direction.DESC, "forks")
        );
    }

    @Test
    void shouldSortRepositoriesByLastUpdated() {

        List<GithubRepository> repositories = List.of(
                new GithubRepository(
                        5L, "recent-project", "Recently updated project",
                        "user5", "Java", 400, 80,
                        java.time.Instant.parse("2026-11-10T10:00:00Z")
                )
        );

        when(repository.findAll(
                Sort.by(Sort.Direction.DESC, "lastUpdated")
        )).thenReturn(repositories);

        List<GithubRepository> result =
                githubSearchService.getStoredRepositories(
                        null, null, "updated"
                );

        assertEquals(1, result.size());
        assertEquals("recent-project", result.get(0).getName());

        verify(repository).findAll(
                Sort.by(Sort.Direction.DESC, "lastUpdated")
        );
    }

    @Test
    void shouldThrowExceptionForInvalidSortValue() {

        IllegalArgumentException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> githubSearchService.getStoredRepositories(
                                null, null, "invalid"
                        )
                );

        assertEquals(
                "Invalid sort value. Allowed values: stars, forks, updated",
                exception.getMessage()
        );
    }

    @Test
    void shouldFilterRepositoriesByLanguageAndMinimumStars() {

        List<GithubRepository> repositories = List.of(
                new GithubRepository(
                        6L, "spring-project", "Spring project",
                        "user6", "Java", 1500, 300, null
                )
        );

        when(repository.findByLanguageIgnoreCaseAndStarsGreaterThanEqual(
                "Java",
                500,
                Sort.by(Sort.Direction.DESC, "stars")
        )).thenReturn(repositories);

        List<GithubRepository> result =
                githubSearchService.getStoredRepositories(
                        "Java", 500, null
                );

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getLanguage());
        assertEquals(1500, result.get(0).getStars());

        verify(repository).findByLanguageIgnoreCaseAndStarsGreaterThanEqual(
                "Java",
                500,
                Sort.by(Sort.Direction.DESC, "stars")
        );
    }


    @Test
    void shouldFetchAndSaveRepositoriesFromGithub()
            throws Exception {

        GithubSearchRequest request =
                new GithubSearchRequest("spring boot", "Java", "stars");

        GithubApiResponse.GithubOwnerDto owner =
                new GithubApiResponse.GithubOwnerDto("test-user");

        GithubApiResponse.GithubRepositoryDto dto =
                new GithubApiResponse.GithubRepositoryDto(
                        101L,
                        "spring-boot-demo",
                        "Spring Boot demo",
                        "Java",
                        500,
                        100,
                        java.time.Instant.parse("2026-11-10T10:00:00Z"),
                        owner
                );

        GithubApiResponse apiResponse =
                new GithubApiResponse(List.of(dto));

        when(githubApiClient.searchRepositories(
                "spring boot", "Java", "stars"
        )).thenReturn("{}");

        when(objectMapper.readValue(
                "{}", GithubApiResponse.class
        )).thenReturn(apiResponse);

        when(repository.findById(101L))
                .thenReturn(java.util.Optional.empty());

        when(repository.saveAll(
                org.mockito.ArgumentMatchers.<GithubRepository>anyList()
        )).thenAnswer(invocation -> invocation.getArgument(0));

        List<GithubRepository> result =
                githubSearchService.searchAndSaveRepositories(request);

        assertEquals(1, result.size());
        assertEquals("spring-boot-demo", result.get(0).getName());
        assertEquals("test-user", result.get(0).getOwner());
        assertEquals(500, result.get(0).getStars());

        verify(githubApiClient).searchRepositories(
                "spring boot", "Java", "stars"
        );

        verify(repository).saveAll(
                org.mockito.ArgumentMatchers.<GithubRepository>anyList()
        );
    }


    @Test
    void shouldThrowExceptionWhenGithubReturnsEmptyResponse() {

        GithubSearchRequest request =
                new GithubSearchRequest("spring boot", "Java", "stars");

        when(githubApiClient.searchRepositories(
                "spring boot", "Java", "stars"
        )).thenReturn("");

        IllegalStateException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        IllegalStateException.class,
                        () -> githubSearchService.searchAndSaveRepositories(request)
                );

        assertEquals(
                "GitHub returned an empty response",
                exception.getMessage()
        );

        verify(githubApiClient).searchRepositories(
                "spring boot", "Java", "stars"
        );
    }


    @Test
    void shouldThrowExceptionWhenGithubReturnsInvalidJson() throws Exception {

        GithubSearchRequest request =
                new GithubSearchRequest("spring boot", "Java", "stars");

        when(githubApiClient.searchRepositories(
                "spring boot", "Java", "stars"
        )).thenReturn("invalid-json");

        when(objectMapper.readValue(
                "invalid-json", GithubApiResponse.class
        )).thenThrow(
                new com.fasterxml.jackson.core.JsonParseException(
                        null, "Invalid JSON response"
                )
        );

        IllegalStateException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        IllegalStateException.class,
                        () -> githubSearchService.searchAndSaveRepositories(request)
                );

        assertEquals(
                "Unable to process GitHub API response",
                exception.getMessage()
        );
    }


}
