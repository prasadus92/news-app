package com.upday.news.controller;

import com.upday.news.Application;
import com.upday.news.controller.errors.ApplicationExceptionHandler;
import com.upday.news.dao.Author;
import com.upday.news.dto.AuthorDto;
import com.upday.news.dto.mapper.ArticleMapper;
import com.upday.news.dto.mapper.AuthorMapper;
import com.upday.news.repository.ArticleRepository;
import com.upday.news.repository.AuthorRepository;
import com.upday.news.service.AuthorService;
import com.upday.news.service.impl.AuthorServiceImpl;
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
public class AuthorControllerIntegrationTest {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";

    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";

    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Author createEntity(EntityManager em) {
        Author author = new Author()
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME);
        return author;
    }

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorMapper authorMapper;

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

    private MockMvc restAuthorMockMvc;

    private Author author;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AuthorService authorService = new AuthorServiceImpl(authorRepository, authorMapper, articleRepository, articleMapper);
        AuthorController authorController = new AuthorController(authorService);
        this.restAuthorMockMvc = MockMvcBuilders.standaloneSetup(authorController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(applicationExceptionHandler)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        author = createEntity(em);
    }

    @Test
    @Transactional
    public void createAuthor() throws Exception {
        int databaseSizeBeforeCreate = authorRepository.findAll().size();

        // Create the Author
        AuthorDto authorDto = authorMapper.toDto(author);
        restAuthorMockMvc.perform(post("/api/v1/authors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(authorDto)))
                .andExpect(status().isCreated());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeCreate + 1);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testAuthor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    public void createAuthorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = authorRepository.findAll().size();

        // Create the Author with an existing ID
        author.setId(1L);
        AuthorDto authorDto = authorMapper.toDto(author);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuthorMockMvc.perform(post("/api/v1/authors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(authorDto)))
                .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllAuthors() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList
        restAuthorMockMvc.perform(get("/api/v1/authors?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(author.getId().intValue())))
                .andExpect(jsonPath("$.[*].first_name").value(hasItem(DEFAULT_FIRST_NAME.toString())))
                .andExpect(jsonPath("$.[*].last_name").value(hasItem(DEFAULT_LAST_NAME.toString())));
    }

    @Test
    @Transactional
    public void getAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get the author
        restAuthorMockMvc.perform(get("/api/v1/authors/{id}", author.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(author.getId().intValue()))
                .andExpect(jsonPath("$.first_name").value(DEFAULT_FIRST_NAME.toString()))
                .andExpect(jsonPath("$.last_name").value(DEFAULT_LAST_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAuthor() throws Exception {
        // Get the author
        restAuthorMockMvc.perform(get("/api/v1/authors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);
        int databaseSizeBeforeUpdate = authorRepository.findAll().size();

        // Update the author
        Author updatedAuthor = authorRepository.getOne(author.getId());
        updatedAuthor
                .firstName(UPDATED_FIRST_NAME)
                .lastName(UPDATED_LAST_NAME);
        AuthorDto authorDto = authorMapper.toDto(updatedAuthor);

        restAuthorMockMvc.perform(put("/api/v1/authors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(authorDto)))
                .andExpect(status().isOk());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testAuthor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().size();

        // Create the Author
        AuthorDto authorDto = authorMapper.toDto(author);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAuthorMockMvc.perform(put("/api/v1/authors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(authorDto)))
                .andExpect(status().isCreated());

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);
        int databaseSizeBeforeDelete = authorRepository.findAll().size();

        // Get the author
        restAuthorMockMvc.perform(delete("/api/v1/authors/{id}", author.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Author> authorList = authorRepository.findAll();
        assertThat(authorList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Author.class);
        Author author1 = new Author();
        author1.setId(1L);
        Author author2 = new Author();
        author2.setId(author1.getId());
        assertThat(author1).isEqualTo(author2);
        author2.setId(2L);
        assertThat(author1).isNotEqualTo(author2);
        author1.setId(null);
        assertThat(author1).isNotEqualTo(author2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuthorDto.class);
        AuthorDto authorDto1 = new AuthorDto();
        authorDto1.setId(1L);
        AuthorDto authorDto2 = new AuthorDto();
        assertThat(authorDto1).isNotEqualTo(authorDto2);
        authorDto2.setId(authorDto1.getId());
        assertThat(authorDto1).isEqualTo(authorDto2);
        authorDto2.setId(2L);
        assertThat(authorDto1).isNotEqualTo(authorDto2);
        authorDto1.setId(null);
        assertThat(authorDto1).isNotEqualTo(authorDto2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(authorMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(authorMapper.fromId(null)).isNull();
    }
}