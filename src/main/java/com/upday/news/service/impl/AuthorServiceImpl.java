package com.upday.news.service.impl;

import com.upday.news.dao.Article;
import com.upday.news.dao.Author;
import com.upday.news.dto.ArticleDto;
import com.upday.news.dto.AuthorDto;
import com.upday.news.dto.mapper.ArticleMapper;
import com.upday.news.dto.mapper.AuthorMapper;
import com.upday.news.repository.ArticleRepository;
import com.upday.news.repository.AuthorRepository;
import com.upday.news.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("authorService")
public class AuthorServiceImpl implements AuthorService {

    private final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleMapper articleMapper;

    public AuthorServiceImpl(final AuthorRepository authorRepository,
                             final AuthorMapper authorMapper,
                             final ArticleRepository articleRepository,
                             final ArticleMapper articleMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
    }

    @Override
    public AuthorDto createAuthor(AuthorDto authorDto) {
        Author author = authorMapper.toEntity(authorDto);
        author = authorRepository.save(author);
        AuthorDto result = authorMapper.toDto(author);
        return result;
    }

    @Override
    public AuthorDto updateAuthor(AuthorDto authorDto) {
        Author author = authorMapper.toEntity(authorDto);
        author = authorRepository.save(author);
        AuthorDto result = authorMapper.toDto(author);
        return result;
    }

    @Override
    public List<AuthorDto> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        return authorMapper.toDto(authors);
    }

    @Override
    public AuthorDto getAuthor(Long id) {

        Optional<Author> author = authorRepository.findById(id);
        if(author.isPresent()){
            AuthorDto authorDto = authorMapper.toDto(author.get());
            return authorDto;
        }
        return null;
    }

    @Override
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    @Override
    public List<ArticleDto> getArticlesByAuthor(Long id) {
        List<Article> articles = articleRepository.findByAuthor(id);
        List<ArticleDto> articlesDto = articleMapper.toDto(articles);
        return articlesDto;
    }
}
