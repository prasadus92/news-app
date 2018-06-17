package com.upday.news.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Keyword entity.
 */
public class KeywordDto implements Serializable {

    private Long id;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }

        KeywordDto keywordDto = (KeywordDto) o;
        if(keywordDto.getId() == null || getId() == null){
            return false;
        }
        return Objects.equals(getId(), keywordDto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "KeywordDto{" +
                "id=" + getId() +
                ", description='" + getDescription() + "'" +
                "}";
    }
}
