package com.upday.news.controller;

import com.upday.news.Application;
import com.upday.news.controller.errors.ApplicationExceptionHandler;
import com.upday.news.dao.Keyword;
import com.upday.news.dto.KeywordDto;
import com.upday.news.dto.mapper.ArticleMapper;
import com.upday.news.dto.mapper.KeywordMapper;
import com.upday.news.repository.ArticleRepository;
import com.upday.news.repository.KeywordRepository;
import com.upday.news.service.KeywordService;
import com.upday.news.service.impl.KeywordServiceImpl;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class KeywordControllerIntegrationTest {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";

    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Keyword createEntity(EntityManager em) {
        Keyword keyword = new Keyword()
                .description(DEFAULT_DESCRIPTION);
        return keyword;
    }

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private KeywordMapper keywordMapper;

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

    private MockMvc restKeywordMockMvc;

    private Keyword keyword;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        KeywordService keywordService = new KeywordServiceImpl(keywordRepository, keywordMapper, articleRepository, articleMapper);
        KeywordController keywordResource = new KeywordController(keywordService);
        this.restKeywordMockMvc = MockMvcBuilders.standaloneSetup(keywordResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(applicationExceptionHandler)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        keyword = createEntity(em);
    }

    @Test
    @Transactional
    public void createKeyword() throws Exception {
        int databaseSizeBeforeCreate = keywordRepository.findAll().size();

        // Create the Keyword
        KeywordDto keywordDto = keywordMapper.toDto(keyword);
        restKeywordMockMvc.perform(post("/api/v1/keywords")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keywordDto)))
                .andExpect(status().isCreated());

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll();
        assertThat(keywordList).hasSize(databaseSizeBeforeCreate + 1);
        Keyword testKeyword = keywordList.get(keywordList.size() - 1);
        assertThat(testKeyword.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createKeywordWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = keywordRepository.findAll().size();

        // Create the Keyword with an existing ID
        keyword.setId(1L);
        KeywordDto keywordDto = keywordMapper.toDto(keyword);

        // An entity with an existing ID cannot be created, so this API call must fail
        restKeywordMockMvc.perform(post("/api/v1/keywords")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keywordDto)))
                .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Keyword> keywordList = keywordRepository.findAll();
        assertThat(keywordList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllKeywords() throws Exception {
        // Initialize the database
        keywordRepository.saveAndFlush(keyword);

        // Get all the keywordList
        restKeywordMockMvc.perform(get("/api/v1/keywords?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(keyword.getId().intValue())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getKeyword() throws Exception {
        // Initialize the database
        keywordRepository.saveAndFlush(keyword);

        // Get the keyword
        restKeywordMockMvc.perform(get("/api/v1/keywords/{id}", keyword.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(keyword.getId().intValue()))
                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingKeyword() throws Exception {
        // Get the keyword
        restKeywordMockMvc.perform(get("/api/v1/keywords/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateKeyword() throws Exception {
        // Initialize the database
        keywordRepository.saveAndFlush(keyword);
        int databaseSizeBeforeUpdate = keywordRepository.findAll().size();

        // Update the keyword
        Keyword updatedKeyword = keywordRepository.getOne(keyword.getId());
        updatedKeyword
                .description(UPDATED_DESCRIPTION);
        KeywordDto keywordDto = keywordMapper.toDto(updatedKeyword);

        restKeywordMockMvc.perform(put("/api/v1/keywords")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keywordDto)))
                .andExpect(status().isOk());

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll();
        assertThat(keywordList).hasSize(databaseSizeBeforeUpdate);
        Keyword testKeyword = keywordList.get(keywordList.size() - 1);
        assertThat(testKeyword.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingKeyword() throws Exception {
        int databaseSizeBeforeUpdate = keywordRepository.findAll().size();

        // Create the Keyword
        KeywordDto keywordDto = keywordMapper.toDto(keyword);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restKeywordMockMvc.perform(put("/api/v1/keywords")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(keywordDto)))
                .andExpect(status().isCreated());

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll();
        assertThat(keywordList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteKeyword() throws Exception {
        // Initialize the database
        keywordRepository.saveAndFlush(keyword);
        int databaseSizeBeforeDelete = keywordRepository.findAll().size();

        // Get the keyword
        restKeywordMockMvc.perform(delete("/api/v1/keywords/{id}", keyword.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Keyword> keywordList = keywordRepository.findAll();
        assertThat(keywordList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Keyword.class);
        Keyword keyword1 = new Keyword();
        keyword1.setId(1L);
        Keyword keyword2 = new Keyword();
        keyword2.setId(keyword1.getId());
        assertThat(keyword1).isEqualTo(keyword2);
        keyword2.setId(2L);
        assertThat(keyword1).isNotEqualTo(keyword2);
        keyword1.setId(null);
        assertThat(keyword1).isNotEqualTo(keyword2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(KeywordDto.class);
        KeywordDto keywordDto1 = new KeywordDto();
        keywordDto1.setId(1L);
        KeywordDto keywordDTO2 = new KeywordDto();
        assertThat(keywordDto1).isNotEqualTo(keywordDTO2);
        keywordDTO2.setId(keywordDto1.getId());
        assertThat(keywordDto1).isEqualTo(keywordDTO2);
        keywordDTO2.setId(2L);
        assertThat(keywordDto1).isNotEqualTo(keywordDTO2);
        keywordDto1.setId(null);
        assertThat(keywordDto1).isNotEqualTo(keywordDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(keywordMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(keywordMapper.fromId(null)).isNull();
    }
}