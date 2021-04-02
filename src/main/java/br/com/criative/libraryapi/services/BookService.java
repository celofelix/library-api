package br.com.criative.libraryapi.services;

import br.com.criative.libraryapi.models.Book;

import java.util.Optional;

public interface BookService {

    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Long id);
}
