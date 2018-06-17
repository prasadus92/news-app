package com.upday.news.service;

import com.upday.news.dto.ArticleDto;

import java.time.ZonedDateTime;
import java.util.List;

public interface ArticleService {

    ArticleDto createOrUpdateArticle(ArticleDto articleDto);

    List<ArticleDto> getArticles(ZonedDateTime startDate, ZonedDateTime endDate);

    ArticleDto getArticle(Long id);

    void deleteArticle(Long id);
}
