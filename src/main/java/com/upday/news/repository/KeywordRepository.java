package com.upday.news.repository;

import com.upday.news.dao.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Keyword entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    @Query("select keyword from Keyword keyword where keyword.description = :description")
    Keyword findByDescription(@Param("description") String description);
}
