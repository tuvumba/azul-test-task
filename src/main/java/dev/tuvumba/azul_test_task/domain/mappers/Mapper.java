package dev.tuvumba.azul_test_task.domain.mappers;

import java.util.List;


/**
 * A base interface for a mapper.
 *
 * @param <Entity> The entity for mapping to corresponding DTO.
 * @param <Dto> The DTO to which this class maps.
 */
public interface Mapper<Entity, Dto> {
    Entity toEntity(Dto dto);
    Dto toDto(Entity entity);
    List<Dto> toDtoList(List<Entity> entities);
    List<Entity> toEntityList(List<Dto> dtos);
}
