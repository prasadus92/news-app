package com.upday.news.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the Article entity.
 */
public class ArticleDto implements Serializable {

    private Long id;

    private String header;

    private String description;

    private String text;

    @JsonProperty(value = "publication_date")
    private ZonedDateTime publicationDate;

    private List<AuthorDto> authors = new ArrayList<>();

    private List<KeywordDto> keywords = new ArrayList<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ZonedDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(ZonedDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<AuthorDto> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorDto> authors) {
        this.authors = authors;
    }

    public List<KeywordDto> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<KeywordDto> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }

        ArticleDto articleDto = (ArticleDto) o;
        if(articleDto.getId() == null || getId() == null){
            return false;
        }
        return Objects.equals(getId(), articleDto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ArticleDto{" +
                "id=" + id +
                ", header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", text='" + text + '\'' +
                ", publicationDate=" + publicationDate +
                ", authors=" + authors +
                ", keywords=" + keywords +
                '}';
    }
}
