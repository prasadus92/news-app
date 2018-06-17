package com.upday.news.service.impl;

import com.upday.news.dao.Article;
import com.upday.news.dto.ArticleDto;
import com.upday.news.dto.mapper.ArticleMapper;
import com.upday.news.repository.ArticleRepository;
import com.upday.news.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Component("articleService")
public class ArticleServiceImpl implements ArticleService {

    private final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleMapper articleMapper;

    public ArticleServiceImpl(final ArticleRepository articleRepository, final ArticleMapper articleMapper) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
    }

    @Override
    public ArticleDto createOrUpdateArticle(ArticleDto articleDto) {

        Article article = articleMapper.toEntity(articleDto);
        article = articleRepository.save(article);
        ArticleDto result = articleMapper.toDto(article);
        return result;
    }

    @Override
    public List<ArticleDto> getArticles(ZonedDateTime startDate, ZonedDateTime endDate) {

        List<Article> articles;
        if(startDate == null && endDate == null){
            articles = articleRepository.findAllWithEagerRelationships();
        } else {
            final ZonedDateTime validStartDate = startDate == null ? ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()) : startDate;
            final ZonedDateTime validEndDate = endDate == null ? ZonedDateTime.now() : endDate;
            articles = articleRepository.findByPublicationDateBetween(validStartDate, validEndDate);
        }
        return articleMapper.toDto(articles);
    }

    @Override
    public ArticleDto getArticle(Long id) {

        Article article;
        try {
            article = articleRepository.findOneWithEagerRelationships(id);
        } catch(EntityNotFoundException ex) {
            log.error("Article not found in the repository.", ex);
            return null;
        }
        ArticleDto articleDto = articleMapper.toDto(article);
        return articleDto;
    }

    @Override
    public void deleteArticle(Long id) {

        articleRepository.deleteById(id);
    }
}
