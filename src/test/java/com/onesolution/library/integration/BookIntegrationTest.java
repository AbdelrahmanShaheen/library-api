package com.onesolution.library.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onesolution.library.LibraryApplication;
import com.onesolution.library.dto.BookRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK ,classes = LibraryApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application.properties")
public class BookIntegrationTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final String baseUrl = "/api/books";
    @Test
    @Sql(statements = "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM book",
            "DELETE FROM author"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testAddBook() throws Exception {
        BookRequest bookRequest = new BookRequest("book 1", "description 1", 1L, 10, "genre 1");

        MvcResult mvcResult = mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
    }
    @Test
    @Sql(statements = {
            "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1')",
            "INSERT INTO book (title, description, author_id, quantity, available_quantity, genre, published_date) VALUES ('Book 1', 'Description 1', 1, 10, 10, 'Genre 1', '2022-01-01T00:00:00'), ('Book 2', 'Description 2', 1, 10, 10, 'Genre 2', '2022-01-01T00:00:00')"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM book",
            "DELETE FROM author"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetAllBooks() throws Exception {
        MvcResult mvcResult = mvc.perform(get(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("\"totalElements\":2");
    }
    @Test
    @Sql(statements = {
            "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1')",
            "INSERT INTO book (id, title, description, author_id, quantity, available_quantity, genre, published_date) VALUES (1, 'Book 1', 'Description 1', 1, 10, 10, 'Genre 1', '2022-01-01T00:00:00')"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM book",
            "DELETE FROM author"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetBookById() throws Exception {
        MvcResult mvcResult = mvc.perform(get(baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("\"title\":\"Book 1\"");
    }
    @Test
    @Sql(statements = {
            "INSERT INTO author (id ,name, biography) VALUES (1 ,'Author 1', 'Biography 1')",
            "INSERT INTO book (id, title, description, author_id, quantity, available_quantity, genre, published_date) VALUES (1, 'Book 1', 'Description 1', 1, 10, 10, 'Genre 1', '2022-01-01T00:00:00')"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM book",
            "DELETE FROM author"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdateBook() throws Exception {
        BookRequest bookRequest = new BookRequest("Updated Book", "Updated Description", 1L, 10, "Updated Genre");

        MvcResult mvcResult = mvc.perform(put(baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }
    @Test
    @Sql(statements = {
            "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1')",
            "INSERT INTO book (id, title, description, author_id, quantity, available_quantity, genre, published_date) VALUES (1, 'Book 1', 'Description 1', 1, 10, 10, 'Genre 1', '2022-01-01T00:00:00')"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM book",
            "DELETE FROM author"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testDeleteBook() throws Exception {
        MvcResult mvcResult = mvc.perform(delete(baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(204);
    }
    @Test
    @Sql(statements = {
            "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1')",
            "INSERT INTO book (id, title, description, author_id, quantity, available_quantity, genre, published_date) VALUES (1, 'Book 1', 'Description 1', 1, 10, 10, 'Genre 1', '2022-01-01T00:00:00')"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM book",
            "DELETE FROM author"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testSearchBooksBy() throws Exception {
        MvcResult mvcResult = mvc.perform(get(baseUrl + "/search")
                        .param("title", "Book 1")
                        .param("genre", "Genre 1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(mvcResult.getResponse().getContentAsString()).contains("\"totalElements\":1");
    }
}
