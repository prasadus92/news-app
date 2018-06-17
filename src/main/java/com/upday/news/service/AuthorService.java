package com.upday.news.service;

import com.upday.news.dto.ArticleDto;
import com.upday.news.dto.AuthorDto;

import java.util.List;

public interface AuthorService {

    AuthorDto createAuthor(AuthorDto authorDto);

    AuthorDto updateAuthor(AuthorDto authorDto);

    List<AuthorDto> getAllAuthors();

    AuthorDto getAuthor(Long id);

    void deleteAuthor(Long id);

    List<ArticleDto> getArticlesByAuthor(Long id);
}
