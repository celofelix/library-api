package br.com.criative.libraryapi.repositories;

import br.com.criative.libraryapi.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);
}
