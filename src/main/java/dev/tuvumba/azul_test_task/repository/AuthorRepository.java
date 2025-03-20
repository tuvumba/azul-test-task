package dev.tuvumba.azul_test_task.repository;

import dev.tuvumba.azul_test_task.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findByDisplayNameContainingIgnoreCase(String name);
}
