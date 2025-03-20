package dev.tuvumba.azul_test_task.domain.mappers;

import dev.tuvumba.azul_test_task.domain.Genre;
import dev.tuvumba.azul_test_task.domain.dto.GenreDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GenreMapper implements Mapper<Genre, GenreDto> {
    private final ModelMapper modelMapper;

    public GenreMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Genre toEntity(GenreDto genreDto) {
        return  modelMapper.map(genreDto, Genre.class);
    }

    @Override
    public GenreDto toDto(Genre genre) {
        return  modelMapper.map(genre, GenreDto.class);
    }

    @Override
    public List<GenreDto> toDtoList(List<Genre> genres) {
        return genres.stream().map(this::toDto).toList();
    }

    @Override
    public List<Genre> toEntityList(List<GenreDto> genreDtos) {
        return genreDtos.stream().map(this::toEntity).toList();
    }

}
