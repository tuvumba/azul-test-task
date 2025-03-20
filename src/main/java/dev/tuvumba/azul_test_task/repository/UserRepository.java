package dev.tuvumba.azul_test_task.repository;

import dev.tuvumba.azul_test_task.domain.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
