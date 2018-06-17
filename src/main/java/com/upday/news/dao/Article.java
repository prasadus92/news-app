package com.upday.news.dao;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@ApiModel(description = "The Article entity.")
@Entity
@Table(name = "article")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "header", nullable = false)
    private String header;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "text")
    private String text;

    @Column(name = "publication_date", nullable = false)
    private ZonedDateTime publicationDate;

    @ManyToMany(cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "article_author",
            joinColumns = @JoinColumn(name = "articles_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authors_id", referencedColumnName = "id"))
    private Set<Author> authors = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "article_keyword",
            joinColumns = @JoinColumn(name = "articles_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "keywords_id", referencedColumnName = "id"))
    private Set<Keyword> keywords = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Article header(String header) {
        this.header = header;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Article description(String description) {
        this.description = description;
        return this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Article text(String text) {
        this.text = text;
        return this;
    }

    public ZonedDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(ZonedDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Article publicationDate(ZonedDateTime publicationDate) {
        this.publicationDate = publicationDate;
        return this;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Article authors(Set<Author> authors) {
        this.authors = authors;
        return this;
    }

    public Article addAuthor(Author author) {
        this.authors.add(author);
        return this;
    }

    public Article removeAuthor(Author author) {
        this.authors.remove(author);
        return this;
    }

    public Set<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<Keyword> keywords) {
        this.keywords = keywords;
    }

    public Article keywords(Set<Keyword> keywords) {
        this.keywords = keywords;
        return this;
    }

    public Article addKeyword(Keyword keyword) {
        this.keywords.add(keyword);
        return this;
    }

    public Article removeKeyword(Keyword keyword) {
        this.keywords.remove(keyword);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Article article = (Article) o;
        if(article.getId() == null || getId() == null){
            return false;
        }
        return Objects.equals(getId(), article.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + getId() +
                ", header='" + getHeader() + "'" +
                ", description='" + getDescription() + "'" +
                ", text='" + getText() + "'" +
                ", publicationDate='" + getPublicationDate() + "'" +
                "}";
    }
}
