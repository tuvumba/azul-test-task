package dev.tuvumba.azul_test_task.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 *  Represents an Author with username(unique) and display name.
 *  This class is simplistic and could be greatly expanded
 *  if we wish to implement additional functionality
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"books"})
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String displayName;
    private String description;

    @ManyToMany(mappedBy = "authors")
    private List<Book> books = new ArrayList<>();
}
