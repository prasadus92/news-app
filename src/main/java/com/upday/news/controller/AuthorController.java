package com.upday.news.controller;

import com.upday.news.controller.util.HeaderUtil;
import com.upday.news.dto.ArticleDto;
import com.upday.news.dto.AuthorDto;
import com.upday.news.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * REST controller for managing Author.
 */
@RestController
@RequestMapping("/api/v1")
public class AuthorController {

    private static final String ENTITY_NAME = "author";

    private final Logger log = LoggerFactory.getLogger(AuthorController.class);

    @Autowired
    private AuthorService authorService;

    public AuthorController(final AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     * POST  /authors : Create a new author.
     *
     * @param authorDto the authorDto to create
     * @return the ResponseEntity with status 201 (Created) and with body the new authorDto, or with status 400 (Bad Request) if the author has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/authors")
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody AuthorDto authorDto) throws URISyntaxException {
        log.debug("REST request to save Author : {}", authorDto);

        if(authorDto.getId() != null){
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new author should not already have an ID")).body(null);
        }

        AuthorDto result = authorService.createAuthor(authorDto);
        return ResponseEntity.created(new URI("/api/v1/authors/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /authors : Updates an existing author.
     *
     * @param authorDto the authorDto to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated authorDto,
     * or with status 400 (Bad Request) if the authorDto is not valid,
     * or with status 500 (Internal Server ErrorDto) if the authorDto couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/authors")
    public ResponseEntity<AuthorDto> updateAuthor(@RequestBody AuthorDto authorDto) throws URISyntaxException {
        log.debug("REST request to update Author : {}", authorDto);

        if(authorDto.getId() == null){
            return createAuthor(authorDto);
        }

        AuthorDto result = authorService.updateAuthor(authorDto);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, authorDto.getId().toString()))
                .body(result);
    }

    /**
     * GET  /authors : get all the authors.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of authors in body
     */
    @GetMapping("/authors")
    public List<AuthorDto> getAllAuthors() {
        log.debug("REST request to get all Authors");

        return authorService.getAllAuthors();
    }

    /**
     * GET  /authors/:id : get the "id" author.
     *
     * @param id the id of the authorDto to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the authorDto, or with status 404 (Not Found)
     */
    @GetMapping("/authors/{id}")
    public ResponseEntity<AuthorDto> getAuthor(@PathVariable Long id) {
        log.debug("REST request to get Author : {}", id);

        AuthorDto authorDto = authorService.getAuthor(id);
        if(authorDto != null){
            return ResponseEntity.ok(authorDto);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE  /authors/:id : delete the "id" author.
     *
     * @param id the id of the authorDto to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        log.debug("REST request to delete Author : {}", id);

        authorService.deleteAuthor(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * GET  /authors/:id : get articles by the "id" author.
     *
     * @param id the id of the author
     * @return the ResponseEntity with status 200 (OK) and with body the List<ArticleDto>, or with status 404 (Not Found)
     */
    @GetMapping("/authors/{id}/articles")
    public ResponseEntity<List<ArticleDto>> getArticlesByAuthor(@PathVariable Long id) {
        log.debug("REST request to get Author : {}", id);

        List<ArticleDto> articles = authorService.getArticlesByAuthor(id);
        if(articles != null){
            return ResponseEntity.ok(articles);
        }
        return ResponseEntity.notFound().build();
    }
}
