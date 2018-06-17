package com.upday.news.dto.mapper;

import com.upday.news.dao.Author;
import com.upday.news.dto.AuthorDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface AuthorMapper extends EntityMapper<AuthorDto, Author> {

    default Author fromId(Long id) {
        if(id == null){
            return null;
        }
        Author author = new Author();
        author.setId(id);
        return author;
    }
}
