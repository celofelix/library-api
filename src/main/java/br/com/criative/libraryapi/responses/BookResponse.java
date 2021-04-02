package br.com.criative.libraryapi.responses;

import br.com.criative.libraryapi.models.Book;
import org.hibernate.validator.constraints.NotBlank;

public class BookResponse {

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @NotBlank
    private String isbn;

    public BookResponse() {
    }

    public BookResponse( @NotBlank String title, @NotBlank String author, @NotBlank String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public Book toModelBook() {
        return new Book(title, author, isbn);
    }

    @Override
    public String toString() {
        return "BookResponse{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
