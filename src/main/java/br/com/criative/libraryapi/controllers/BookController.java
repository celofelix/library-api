package br.com.criative.libraryapi.controllers;

import br.com.criative.libraryapi.models.Book;
import br.com.criative.libraryapi.responses.BookResponse;
import br.com.criative.libraryapi.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("{id}")
    public BookResponse get(@PathVariable Long id) {

        return service.getById(id).map(book -> book.toResponseBook())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        service.delete(book.getId());
    }

    @PutMapping("{id}")
    public BookResponse update(@PathVariable Long id, BookResponse response) {

        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        book.setTitle(response.getTitle());
        book.setAuthor(response.getAuthor());
        book = service.update(book);
        System.out.println(book);
        return book.toResponseBook();
    }

}

