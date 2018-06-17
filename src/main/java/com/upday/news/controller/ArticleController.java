package com.upday.news.controller;

import com.upday.news.controller.util.HeaderUtil;
import com.upday.news.dto.ArticleDto;
import com.upday.news.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * REST controller for managing Article.
 */
@RestController
@RequestMapping("/api/v1")
public class ArticleController {

    private static final String ENTITY_NAME = "article";

    private final Logger log = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    ArticleService articleService;

    public ArticleController(final ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * POST  /articles : Create a new article.
     *
     * @param articleDto the articleDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new articleDto, or with status 400 (Bad Request) if the article has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/articles")
    public ResponseEntity<ArticleDto> createArticle(@RequestBody ArticleDto articleDto) throws URISyntaxException {
        log.debug("REST request to save Article : {}", articleDto);
        if(articleDto.getId() != null){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new article should not already have an ID")).body(null);
        }

        ArticleDto result = articleService.createOrUpdateArticle(articleDto);
        return ResponseEntity.created(new URI("/api/articles/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /articles : Updates an existing article.
     *
     * @param articleDto the articleDto to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated articleDto,
     * or with status 400 (Bad Request) if the articleDto is not valid,
     * or with status 500 (Internal Server ErrorDto) if the articleDto couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/articles")
    public ResponseEntity<ArticleDto> updateArticle(@RequestBody ArticleDto articleDto) throws URISyntaxException {
        log.debug("REST request to update Article : {}", articleDto);
        if(articleDto.getId() == null){
            return createArticle(articleDto);
        }
        ArticleDto result = articleService.createOrUpdateArticle(articleDto);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, articleDto.getId().toString()))
                .body(result);
    }

    /**
     * GET  /articles : get articles.
     *
     * @param startDate lower bound to filter by date (optional)
     * @param endDate   upper bound to filter by date (optional)
     * @return the ResponseEntity with status 200 (OK) and the list of articles in body
     */
    @GetMapping("/articles")
    public List<ArticleDto> getArticles(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) {
        log.debug("REST request to get all Articles");
        return articleService.getArticles(startDate, endDate);
    }

    /**
     * GET  /articles/:id : get the "id" article.
     *
     * @param id the id of the articleDto to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the articleDto, or with status 404 (Not Found)
     */
    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleDto> getArticle(@PathVariable Long id) {
        log.debug("REST request to get Article : {}", id);

        ArticleDto articleDto = articleService.getArticle(id);
        if(articleDto != null){
            return ResponseEntity.ok().body(articleDto);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE  /articles/:id : delete the "id" article.
     *
     * @param id the id of the articleDto to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        log.debug("REST request to delete Article : {}", id);
        articleService.deleteArticle(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
