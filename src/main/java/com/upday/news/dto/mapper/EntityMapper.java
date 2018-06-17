package com.upday.news.dto.mapper;

import java.util.List;

/**
 * Generic DTO to entity mapper.
 *
 * @param <D> - DTO type parameter.
 * @param <E> - Entity type parameter.
 */

public interface EntityMapper<D, E> {

    public E toEntity(D dto);

    public D toDto(E entity);

    public List<E> toEntity(List<D> dtoList);

    public List<D> toDto(List<E> entityList);
}
