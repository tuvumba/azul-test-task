package dev.tuvumba.azul_test_task.service.base;

import java.util.List;
import java.util.Optional;

/**
 * A simple CRUD interface.
 * @param <Entity> Type of entity the CRUD service will use
 * @param <ID> Type of ID the said entity uses
 */
public interface CrudService<Entity, ID> {
    Optional<Entity> save (Entity entity);
    List<Entity> findAll();
    Optional<Entity> findById(ID id);
    void delete(ID id);
}
