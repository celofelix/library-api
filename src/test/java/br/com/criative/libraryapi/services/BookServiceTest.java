package br.com.criative.libraryapi.services;

import br.com.criative.libraryapi.handler.IsbnException;
import br.com.criative.libraryapi.models.Book;
import br.com.criative.libraryapi.repositories.BookRepository;
import br.com.criative.libraryapi.services.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setService() {
        service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void SaveBookTest() {
        Book book = new Book("Hobbit", "Tolkien", "123123");

        /* Simulando o comportamento do método existsNyIsbn
        Com essa simulação ele está retornando false.
        Nesse caso está sendo passado um objeto válido sem isbn repetido.
        Mas para garantir foi feito essa simulação do existsByIsbn retornando false*/
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        /* Simulando o comportamento de persistência do Repository de um objeto Book
        Não foi utilizado o BDDMockito devido ser apenas um teste únitário na camada de serviço
        O BDDMockito foi usado anteriormente devido a estar ligado a aplicação por completo
        Como se trata apenas de teste unitário pode ser utilizado o Mockito */
        Mockito.when(repository.save(book))
                .thenReturn(new Book("Hobbit", "Tolkien", "123123"));

        Book savedBook = service.save(book);

        Assertions.assertThat(savedBook.getTitle()).isEqualTo("Hobbit");
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Tolkien");
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("123123");
    }

    @Test
    @DisplayName("Deve lançar erro de isbn duplicado")
    public void shouldNotSaveWithIsbnDuplicated() {

        Book book = new Book(
                "Hobbit",
                "Tolkien",
                "123123");

        /* Simulando o comportamento existsByIsbn() do repository.
        Assim quando o mock chamar o repository ele vai retornar true e vai lançar a exceção */
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        String mensagem = "Isbn já foi cadastrado";

        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        Assertions.assertThat(exception)
                .isInstanceOf(IsbnException.class)
                .hasMessage(mensagem);

        Mockito.verify(repository, Mockito.never()).save(book);


    }

}
