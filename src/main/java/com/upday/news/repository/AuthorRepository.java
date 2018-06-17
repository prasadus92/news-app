package com.upday.news.repository;

import com.upday.news.dao.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Author entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("select author from Author author where author.firstName = :firstName and author.lastName = :lastName")
    Author findAuthorByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
