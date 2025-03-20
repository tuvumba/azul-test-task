package dev.tuvumba.azul_test_task.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;


/**
 *  Represents a book genre. <br>
 *  Uses genre name for ID. <br>
 *  Possible extensions: metadata, subgenres.
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="name")
@ToString(exclude = {"books"})
public class Genre {

    @Id
    private String name;

    @ManyToMany(mappedBy = "genres")
    private List<Book> books = new ArrayList<>();
}


