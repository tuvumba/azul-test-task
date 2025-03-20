package dev.tuvumba.azul_test_task.domain.mappers;

import dev.tuvumba.azul_test_task.domain.Author;
import dev.tuvumba.azul_test_task.domain.dto.AuthorDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorMapper implements Mapper<Author, AuthorDto> {

    private final ModelMapper modelMapper;

    public AuthorMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Author toEntity(AuthorDto authorDto) {
        return modelMapper.map(authorDto, Author.class);
    }

    @Override
    public AuthorDto toDto(Author author) {
       return modelMapper.map(author, AuthorDto.class);
    }

    @Override
    public List<AuthorDto> toDtoList(List<Author> authors) {
        return authors.stream().map(this::toDto).toList();
    }

    @Override
    public List<Author> toEntityList(List<AuthorDto> authorDtos) {
        return authorDtos.stream().map(this::toEntity).toList();
    }
}

