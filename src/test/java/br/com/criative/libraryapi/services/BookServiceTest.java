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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

        /* Simulando o comportamento do método save() na classe de serviço
        Passando o livro criado para ser salvo
        O retorno deve possuir as mesmas informações do livro criado */
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

    @Test
    @DisplayName("Deve obter livro por ID")
    public void getByIdTest() {

        Book book = new Book(
                1L,
                "Hobbit",
                "Tolkien",
                "123123");

        Mockito.when(repository.findById(book.getId())).thenReturn(Optional.of(book));

        Optional<Book> bookFound = service.getById(book.getId());

        Assertions.assertThat(bookFound.isPresent()).isTrue();
        Assertions.assertThat(bookFound.get().getId()).isEqualTo(book.getId());
        Assertions.assertThat(bookFound.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(bookFound.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(bookFound.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio caso o ID do Livro não exista")
    public void bookNotFoundByIdTest() {

        Long id = 1L;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> bookFound = service.getById(id);

        Assertions.assertThat(bookFound.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() {

        Long id = 1L;

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(id));

        Mockito.verify(repository, Mockito.times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao deletar livro por ID null")
    public void deleteByInvalidIDTest() {

        Long id = 1L;

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.delete(null));

        Mockito.verify(repository, Mockito.never()).deleteById(id);
    }

    @Test
    @DisplayName("Deve atualizar livro")
    public void updateBookTest() {

        Book updatingBook = new Book(
                1L,
                "Hobbit",
                "Tolkien",
                "123123");

        Book updatedBook = new Book(
                1L,
                "As duas Torres",
                "Tolkien",
                "123123");

        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        Book newBook = service.update(updatingBook);

        Assertions.assertThat(newBook.getId()).isEqualTo(updatedBook.getId());
        Assertions.assertThat(newBook.getTitle()).isEqualTo(updatedBook.getTitle());
        Assertions.assertThat(newBook.getAuthor()).isEqualTo(updatedBook.getAuthor());
        Assertions.assertThat(newBook.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve ocorrer erro ao atualizar livro inexistente ou null")
    public void updateInvalidBookTest() {
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.update(book));

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros por author e title, realizando paginação")
    public void findBookTest() {

        // Cenário
        Book book = new Book(
                "Hobbit",
                "Tolkien",
                "123123");

        PageRequest pages = PageRequest.of(0, 10);
        List<Book> books = Arrays.asList(book);

        Page<Book> page = new PageImpl(books,
                pages, 1);

        Mockito.when(repository.findAll(
                Mockito.any(Example.class),
                Mockito.any(PageRequest.class)))
                .thenReturn(page);

        // Execução
        Page<Book> bookPage = service.find(book, pages);

        // Verificações
        Assertions.assertThat(bookPage.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(bookPage.getContent()).isEqualTo(books);
        Assertions.assertThat(bookPage.getNumber()).isEqualTo(0);
        Assertions.assertThat(bookPage.getSize()).isEqualTo(10);
    }
}
