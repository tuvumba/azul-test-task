package dev.tuvumba.azul_test_task.repository;

import dev.tuvumba.azul_test_task.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {
    List<Genre> findByNameIsContaining(String name);
}
