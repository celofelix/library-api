package br.com.criative.libraryapi.controllers;

import br.com.criative.libraryapi.handler.IsbnException;
import br.com.criative.libraryapi.models.Book;
import br.com.criative.libraryapi.responses.BookResponse;
import br.com.criative.libraryapi.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/* Anotação para o Spring criar um contexto para rodar os testes.
O contexto é criado a partir da injeção de depência controlada pelo Spring  */
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
/* Anotação serve para fazer testes únitários em um Controller*/
@WebMvcTest
/* Anotação para configurar um objeto que irá fazer as requisições */
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    /* @MockBean serve para criar uma instancia do objeto.
    Assim colocando no contexto de injeção de depência do spring
    Um Mock é uma instancia "fake" dessa forma não altera a classe de origem
    Assim é possível manipular a classe e simular o comportamento da classe */
    @MockBean
    BookService service;

    /* Classe para simular as requições http */
    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {

        Book entitySaved = new Book(
                "Hobbit",
                "Tolkien",
                "123123");

        BookResponse response = new BookResponse(
                "Hobbit",
                "Tolkien",
                "123123");

        /* Classe usada para simular a execução da camada de serviço
        Após a execução ela irá retornar objeto persistido no banco de dados entitySaved
        Nesse ponto é uma simulação de teste de integração da camada de serviço com o repository
        Todos os testes dessa classe são unitários, apenas para os métodos do controller */
        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(entitySaved);

        /* Classe ObjectMapper serve para transformar um objeto em json
        Deve ser usado o método writeValueAsString() e passar o objeto no parâmetro */
        String json = new ObjectMapper().writeValueAsString(response);

        /* Classe MockHttpServletRequestBuilder responsável por montar a requisição HTTP
        post() é o tipo de método http e como parâmetro o endereço da url
        contentType() serve para indicar o tipo do conteúdo enviada
        MediaType.APPLICATION_JSON informa o conteúdo enviado como parâmetro
        accept() serve para informar o conteúdo que o recurso aceita
        contente() serve para enviar o objeto json, deve ser uma string */
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        /* Objeto mvc criado com o MockMvc anteriormente. Serve para disparar o envio da requisição
        perform() é o método que dispara o objeto. Deve ser passado no parâmetro o objeto
        andExpect() serve para apontar a resposta esperada
        status() usado em conjunto com o isCreated() para informar que espera o http status 2001
        jsonPath() serve para montar o objeto json de resposta esperado */
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(response.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(response.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(response.getIsbn()));

    }

    @Test
    @DisplayName("Deve lançar erro de validação para dados incompletos")
    public void createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookResponse());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Não deve criar livro com Isbn duplicado")
    public void notCreateBookWithIsbnDuplicated() throws Exception {

        BookResponse response = new BookResponse(
                "Hobbit",
                "Tolkien",
                "123123");

        String json = new ObjectMapper().writeValueAsString(response);

        String mensagem = "Isbn já foi cadastrado";

        BDDMockito
                .given(service.save(Mockito.any(Book.class)))
                .willThrow(new IsbnException(mensagem));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(mensagem));

    }


}
