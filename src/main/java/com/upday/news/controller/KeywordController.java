package com.upday.news.controller;

import com.upday.news.controller.util.HeaderUtil;
import com.upday.news.dto.ArticleDto;
import com.upday.news.dto.KeywordDto;
import com.upday.news.service.KeywordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * REST controller for managing Keyword.
 */
@RestController
@RequestMapping("/api/v1")
public class KeywordController {

    private static final String ENTITY_NAME = "keyword";

    private final Logger log = LoggerFactory.getLogger(KeywordController.class);

    @Autowired
    private KeywordService keywordService;

    public KeywordController(final KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    /**
     * POST  /keywords : Create a new keyword.
     *
     * @param keywordDto the keywordDto to create
     * @return the ResponseEntity with status 201 (Created) and with body the new keywordDto, or with status 400 (Bad Request) if the keyword has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/keywords")
    public ResponseEntity<KeywordDto> createKeyword(@RequestBody KeywordDto keywordDto) throws URISyntaxException {
        log.debug("REST request to save Keyword : {}", keywordDto);
        if(keywordDto.getId() != null){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new keyword should not already have an ID")).body(null);
        }

        KeywordDto result = keywordService.createKeyword(keywordDto);
        return ResponseEntity.created(new URI("/api/v1/keywords/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /keywords : Updates an existing keyword.
     *
     * @param keywordDto the keywordDto to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated keywordDto,
     * or with status 400 (Bad Request) if the keywordDto is not valid,
     * or with status 500 (Internal Server ErrorDto) if the keywordDto couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/keywords")
    public ResponseEntity<KeywordDto> updateKeyword(@RequestBody KeywordDto keywordDto) throws URISyntaxException {
        log.debug("REST request to update Keyword : {}", keywordDto);
        if(keywordDto.getId() == null){
            return createKeyword(keywordDto);
        }

        KeywordDto result = keywordService.updateKeyword(keywordDto);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, keywordDto.getId().toString()))
                .body(result);
    }

    /**
     * GET  /keywords : get all the keywords.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of keywords in body
     */
    @GetMapping("/keywords")
    public List<KeywordDto> getAllKeywords() {
        log.debug("REST request to get all Keywords");

        return keywordService.getAllKeywords();
    }

    /**
     * GET  /keywords/:id : get the "id" keyword.
     *
     * @param id the id of the keywordDto to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the keywordDto, or with status 404 (Not Found)
     */
    @GetMapping("/keywords/{id}")
    public ResponseEntity<KeywordDto> getKeyword(@PathVariable Long id) {
        log.debug("REST request to get Keyword : {}", id);

        KeywordDto keywordDto = keywordService.getKeyword(id);
        if(keywordDto != null){
            return ResponseEntity.ok(keywordDto);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE  /keywords/:id : delete the "id" keyword.
     *
     * @param id the id of the keywordDto to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/keywords/{id}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable Long id) {
        log.debug("REST request to delete Keyword : {}", id);

        keywordService.deleteKeyword(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * GET  /keywords/:id : get the "id" keyword.
     *
     * @param description description wanted
     * @return the ResponseEntity with status 200 (OK) and with body the List<ArticleDto>, or with status 404 (Not Found)
     */
    @GetMapping("/keywords/{description}/articles")
    public ResponseEntity<List<ArticleDto>> getArticlesByKeyword(@PathVariable String description) {
        log.debug("REST request to get Keyword : {}", description);

        List<ArticleDto> articles = keywordService.getArticlesByKeyword(description);
        if(articles != null){
            return ResponseEntity.ok(articles);
        }
        return ResponseEntity.notFound().build();
    }
}
