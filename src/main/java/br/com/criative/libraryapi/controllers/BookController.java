package br.com.criative.libraryapi.controllers;

import br.com.criative.libraryapi.models.Book;
import br.com.criative.libraryapi.responses.BookResponse;
import br.com.criative.libraryapi.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@RequestBody @Valid BookResponse response) {

        Book book = response.toModelBook();
        book = service.save(book);

        BookResponse responseBook = book.toResponseBook();
        System.out.println(responseBook);
        return responseBook;
    }

}

