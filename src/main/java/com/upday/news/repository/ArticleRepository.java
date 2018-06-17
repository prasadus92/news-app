package com.upday.news.repository;

import com.upday.news.dao.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the Article entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("select distinct article from Article article left join fetch article.authors left join fetch article.keywords")
    List<Article> findAllWithEagerRelationships();

    @Query("select article from Article article left join fetch article.authors left join fetch article.keywords where article.id =:id")
    Article findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select article from Article article left join fetch article.authors author where author.id =:id")
    List<Article> findByAuthor(@Param("id") Long id);

    @Query("select article from Article article left join fetch article.keywords keyword where keyword.description =:description")
    List<Article> findByKeyword(@Param("description") String description);

    @Query("select distinct article from Article article left join fetch article.authors left join fetch article.keywords where article.publicationDate between :startDate and :endDate")
    List<Article> findByPublicationDateBetween(@Param("startDate") ZonedDateTime startDate, @Param("endDate") ZonedDateTime endDate);
}

