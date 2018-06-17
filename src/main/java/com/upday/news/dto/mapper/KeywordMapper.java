package com.upday.news.dto.mapper;

import com.upday.news.dao.Keyword;
import com.upday.news.dto.KeywordDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface KeywordMapper extends EntityMapper<KeywordDto, Keyword> {

    default Keyword fromId(Long id) {
        if(id == null){
            return null;
        }
        Keyword keyword = new Keyword();
        keyword.setId(id);
        return keyword;
    }
}
