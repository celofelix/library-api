package br.com.criative.libraryapi.repositories;

import br.com.criative.libraryapi.models.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")

/* Anotação para indicar serão realizados testes com JPA
Dessa forma ele irá criar uma instância do banco de dados em memoria
Esse banco criado é apenas para o momento dos tests
No momento da execução do teste ele limpa a base de dados
Após finalizar o teste a base de dados também é limpa.
Dessa forma é possível realizar os testes de integração*/
@DataJpaTest
public class BookRepositoryTest {

    /* Objeto usado para criar os cenários
    É um EntityManager apesar usado em tests */
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar true para um isbn existente no banco")
    public void returnTrueWhenIsbnExists() {

        String isbn = "123";

        /* Criando objeto para popular a base e ser persistido
        Deve ser sempre criado um objeto no teste unitário
        No escopo de teste a base criado é sempre limpa antes da execução
        E ela também é excluída após o termino da execução do teste */
        Book book = new Book(
                "Hobbit",
                "Tolkien",
                isbn);

        entityManager.persist(book);
        boolean exists = bookRepository.existsByIsbn(isbn);
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false para um isbn não existente no banco")
    public void returnFalseWhenIsbnNotExists() {

        String isbn = "123";

        /* Criando objeto para popular a base e ser persistido
        Deve ser sempre criado um objeto no teste unitário
        No escopo de teste a base criado é sempre limpa antes da execução
        E ela também é excluída após o termino da execução do teste */
        Book book = new Book(
                "Hobbit",
                "Tolkien",
                "1234");

        entityManager.persist(book);
        boolean exists = bookRepository.existsByIsbn(isbn);
        Assertions.assertThat(exists).isFalse();
    }
}
