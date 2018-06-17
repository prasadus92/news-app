package com.upday.news.service.impl;

import com.upday.news.dao.Article;
import com.upday.news.dao.Keyword;
import com.upday.news.dto.ArticleDto;
import com.upday.news.dto.KeywordDto;
import com.upday.news.dto.mapper.ArticleMapper;
import com.upday.news.dto.mapper.KeywordMapper;
import com.upday.news.repository.ArticleRepository;
import com.upday.news.repository.KeywordRepository;
import com.upday.news.service.KeywordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("keywordService")
public class KeywordServiceImpl implements KeywordService {

    private final Logger log = LoggerFactory.getLogger(KeywordServiceImpl.class);

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private KeywordMapper keywordMapper;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleMapper articleMapper;

    public KeywordServiceImpl(final KeywordRepository keywordRepository,
                              final KeywordMapper keywordMapper,
                              final ArticleRepository articleRepository,
                              final ArticleMapper articleMapper) {
        this.keywordRepository = keywordRepository;
        this.keywordMapper = keywordMapper;
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
    }

    @Override
    public KeywordDto createKeyword(KeywordDto keywordDto) {
        Keyword keyword = keywordMapper.toEntity(keywordDto);
        keyword = keywordRepository.save(keyword);
        KeywordDto result = keywordMapper.toDto(keyword);
        return result;
    }

    @Override
    public KeywordDto updateKeyword(KeywordDto keywordDto) {
        Keyword keyword = keywordMapper.toEntity(keywordDto);
        keyword = keywordRepository.save(keyword);
        KeywordDto result = keywordMapper.toDto(keyword);
        return result;
    }

    @Override
    public List<KeywordDto> getAllKeywords() {
        List<Keyword> keywords = keywordRepository.findAll();
        return keywordMapper.toDto(keywords);
    }

    @Override
    public KeywordDto getKeyword(Long id) {

        Optional<Keyword> keyword = keywordRepository.findById(id);
        if(keyword.isPresent()){
            KeywordDto keywordDto = keywordMapper.toDto(keyword.get());
            return keywordDto;
        }
        return null;
    }

    @Override
    public void deleteKeyword(Long id) {
        keywordRepository.deleteById(id);
    }

    @Override
    public List<ArticleDto> getArticlesByKeyword(String description) {
        List<Article> articles = articleRepository.findByKeyword(description);
        List<ArticleDto> articlesDto = articleMapper.toDto(articles);
        return articlesDto;
    }
}
