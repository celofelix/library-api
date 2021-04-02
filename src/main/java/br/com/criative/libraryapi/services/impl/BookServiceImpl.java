package br.com.criative.libraryapi.services.impl;

import br.com.criative.libraryapi.handler.IsbnException;
import br.com.criative.libraryapi.models.Book;
import br.com.criative.libraryapi.repositories.BookRepository;
import br.com.criative.libraryapi.services.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new IsbnException("Isbn já foi cadastrado");
        }
        return repository.save(book);
    }
}
