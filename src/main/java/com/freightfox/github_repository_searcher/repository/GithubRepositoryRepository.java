
package com.freightfox.github_repository_searcher.repository;

import com.freightfox.github_repository_searcher.entity.GithubRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GithubRepositoryRepository
        extends JpaRepository<GithubRepository, Long> {

    List<GithubRepository> findByLanguageIgnoreCase(
            String language, Sort sort);

    List<GithubRepository> findByStarsGreaterThanEqual(
            Integer minStars, Sort sort);

    List<GithubRepository> findByLanguageIgnoreCaseAndStarsGreaterThanEqual(
            String language, Integer minStars, Sort sort);
}
