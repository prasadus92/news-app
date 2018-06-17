package com.upday.news.service;

import com.upday.news.dto.ArticleDto;
import com.upday.news.dto.KeywordDto;

import java.util.List;

public interface KeywordService {

    KeywordDto createKeyword(KeywordDto keywordDto);

    KeywordDto updateKeyword(KeywordDto keywordDto);

    List<KeywordDto> getAllKeywords();

    KeywordDto getKeyword(Long id);

    void deleteKeyword(Long id);

    List<ArticleDto> getArticlesByKeyword(String description);
}
