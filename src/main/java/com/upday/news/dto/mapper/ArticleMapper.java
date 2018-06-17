package com.upday.news.dto.mapper;

import com.upday.news.dao.Article;
import com.upday.news.dto.ArticleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, KeywordMapper.class,})
public interface ArticleMapper extends EntityMapper<ArticleDto, Article> {

    default Article fromId(Long id) {
        if(id == null){
            return null;
        }
        Article article = new Article();
        article.setId(id);
        return article;
    }
}
