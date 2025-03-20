package dev.tuvumba.azul_test_task.domain.mappers;

import dev.tuvumba.azul_test_task.domain.Book;
import dev.tuvumba.azul_test_task.domain.dto.BookDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookMapper implements Mapper<Book, BookDto> {

    private final ModelMapper modelMapper;

    public BookMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Book toEntity(BookDto bookDto) {
       return modelMapper.map(bookDto, Book.class);
    }

    @Override
    public BookDto toDto(Book book) {
        return modelMapper.map(book, BookDto.class);
    }

    @Override
    public List<BookDto> toDtoList(List<Book> books) {
        return books.stream().map(this::toDto).toList();
    }

    @Override
    public List<Book> toEntityList(List<BookDto> bookDtos) {
        return bookDtos.stream().map(this::toEntity).toList();
    }
}

