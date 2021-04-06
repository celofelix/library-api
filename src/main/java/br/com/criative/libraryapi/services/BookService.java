package br.com.criative.libraryapi.services;

import br.com.criative.libraryapi.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {

    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Long id);

    Book update(Book book);

    Page<Book> find(Book book, Pageable pages);
}
