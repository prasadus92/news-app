package com.upday.news.controller;

import com.upday.news.Application;
import com.upday.news.controller.errors.ApplicationExceptionHandler;
import com.upday.news.dao.Article;
import com.upday.news.dto.ArticleDto;
import com.upday.news.dto.mapper.ArticleMapper;
import com.upday.news.repository.ArticleRepository;
import com.upday.news.service.ArticleService;
import com.upday.news.service.impl.ArticleServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static com.upday.news.controller.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ArticleControllerIntegrationTest {

    private static final String DEFAULT_HEADER = "AAAAAAAAAA";

    private static final String UPDATED_HEADER = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";

    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_TEXT = "AAAAAAAAAA";

    private static final String UPDATED_TEXT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_PUBLICATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);

    private static final ZonedDateTime UPDATED_PUBLICATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Article createEntity(EntityManager em) {
        Article article = new Article()
                .header(DEFAULT_HEADER)
                .description(DEFAULT_DESCRIPTION)
                .text(DEFAULT_TEXT)
                .publicationDate(DEFAULT_PUBLICATION_DATE);
        return article;
    }

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ApplicationExceptionHandler applicationExceptionHandler;

    @Autowired
    private EntityManager em;

    private MockMvc restArticleMockMvc;

    private Article article;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ArticleService articleService = new ArticleServiceImpl(articleRepository, articleMapper);
        ArticleController articleController = new ArticleController(articleService);
        this.restArticleMockMvc = MockMvcBuilders.standaloneSetup(articleController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(applicationExceptionHandler)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        article = createEntity(em);
    }

    @Test
    @Transactional
    public void createArticle() throws Exception {
        int databaseSizeBeforeCreate = articleRepository.findAll().size();

        // Create the Article
        ArticleDto articleDto = articleMapper.toDto(article);
        restArticleMockMvc.perform(post("/api/v1/articles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(articleDto)))
                .andExpect(status().isCreated());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeCreate + 1);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getHeader()).isEqualTo(DEFAULT_HEADER);
        assertThat(testArticle.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testArticle.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testArticle.getPublicationDate()).isEqualTo(DEFAULT_PUBLICATION_DATE);
    }

    @Test
    @Transactional
    public void createArticleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = articleRepository.findAll().size();

        // Create the Article with an existing ID
        article.setId(1L);
        ArticleDto ArticleDto = articleMapper.toDto(article);

        // An entity with an existing ID cannot be created, so this API call must fail
        restArticleMockMvc.perform(post("/api/v1/articles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ArticleDto)))
                .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getArticles() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);

        // Get all the articleList
        restArticleMockMvc.perform(get("/api/v1/articles?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(article.getId().intValue())))
                .andExpect(jsonPath("$.[*].header").value(hasItem(DEFAULT_HEADER.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].text").value(hasItem(DEFAULT_TEXT.toString())))
                .andExpect(jsonPath("$.[*].publication_date").value(hasItem(sameInstant(DEFAULT_PUBLICATION_DATE))));
    }

    @Test
    @Transactional
    public void getArticle() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);

        // Get the article
        restArticleMockMvc.perform(get("/api/v1/articles/{id}", article.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(article.getId().intValue()))
                .andExpect(jsonPath("$.header").value(DEFAULT_HEADER.toString()))
                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
                .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()))
                .andExpect(jsonPath("$.publication_date").value(sameInstant(DEFAULT_PUBLICATION_DATE)));
    }

    @Test
    @Transactional
    public void getNonExistingArticle() throws Exception {
        // Get the article
        restArticleMockMvc.perform(get("/api/v1/articles/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateArticle() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);
        int databaseSizeBeforeUpdate = articleRepository.findAll().size();

        // Update the article
        Article updatedArticle = articleRepository.getOne(article.getId());
        updatedArticle
                .header(UPDATED_HEADER)
                .description(UPDATED_DESCRIPTION)
                .text(UPDATED_TEXT)
                .publicationDate(UPDATED_PUBLICATION_DATE);
        ArticleDto articleDto = articleMapper.toDto(updatedArticle);

        restArticleMockMvc.perform(put("/api/v1/articles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(articleDto)))
                .andExpect(status().isOk());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getHeader()).isEqualTo(UPDATED_HEADER);
        assertThat(testArticle.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testArticle.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testArticle.getPublicationDate()).isEqualTo(UPDATED_PUBLICATION_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().size();

        // Create the Article
        ArticleDto ArticleDto = articleMapper.toDto(article);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restArticleMockMvc.perform(put("/api/v1/articles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ArticleDto)))
                .andExpect(status().isCreated());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteArticle() throws Exception {
        // Initialize the database
        articleRepository.saveAndFlush(article);
        int databaseSizeBeforeDelete = articleRepository.findAll().size();

        // Get the article
        restArticleMockMvc.perform(delete("/api/v1/articles/{id}", article.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Article.class);
        Article article1 = new Article();
        article1.setId(1L);
        Article article2 = new Article();
        article2.setId(article1.getId());
        assertThat(article1).isEqualTo(article2);
        article2.setId(2L);
        assertThat(article1).isNotEqualTo(article2);
        article1.setId(null);
        assertThat(article1).isNotEqualTo(article2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ArticleDto.class);
        ArticleDto ArticleDto1 = new ArticleDto();
        ArticleDto1.setId(1L);
        ArticleDto ArticleDto2 = new ArticleDto();
        assertThat(ArticleDto1).isNotEqualTo(ArticleDto2);
        ArticleDto2.setId(ArticleDto1.getId());
        assertThat(ArticleDto1).isEqualTo(ArticleDto2);
        ArticleDto2.setId(2L);
        assertThat(ArticleDto1).isNotEqualTo(ArticleDto2);
        ArticleDto1.setId(null);
        assertThat(ArticleDto1).isNotEqualTo(ArticleDto2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(articleMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(articleMapper.fromId(null)).isNull();
    }
}