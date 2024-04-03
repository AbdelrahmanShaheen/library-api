package integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.onesolution.library.LibraryApplication;
import com.onesolution.library.dto.BookTransactionRequest;
import com.onesolution.library.dto.BorrowedBookResponse;
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

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK ,classes = LibraryApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application.properties")
public class BookTransactionIntegrationTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final String baseUrl = "/api/books";
    @Test
    @Sql(statements = {
            "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1')",
            "INSERT INTO book (id, title, description, author_id, quantity, available_quantity, genre, published_date) VALUES (1, 'Book 1', 'Description 1', 1, 10, 10, 'Genre 1', '2022-01-01T00:00:00')"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM book_transaction",
            "DELETE FROM book",
            "DELETE FROM author"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testBorrowBook() throws Exception {
        BookTransactionRequest bookTransactionRequest = new BookTransactionRequest("test@example.com");

        MvcResult mvcResult = mvc.perform(post(baseUrl + "/1/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookTransactionRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
    }
    @Test
    @Sql(statements = {
            "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1')",
            "INSERT INTO book (id, title, description, author_id, quantity, available_quantity, genre, published_date) VALUES (1, 'Book 1', 'Description 1', 1, 10, 10, 'Genre 1', '2022-01-01T00:00:00')",
            "INSERT INTO book_transaction (id, book_id, borrower_email, borrowed_date, due_date, status) VALUES (1, 1, 'test@example.com', '2022-01-01T00:00:00', '2022-02-01T00:00:00', 'BORROWED')"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM book_transaction",
            "DELETE FROM book",
            "DELETE FROM author"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testReturnBook() throws Exception {
        BookTransactionRequest bookTransactionRequest = new BookTransactionRequest("test@example.com");

        MvcResult mvcResult = mvc.perform(post(baseUrl + "/1/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookTransactionRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }
    @Test
    @Sql(statements = {
            "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1')",
            "INSERT INTO book (id, title, description, author_id, quantity, available_quantity, genre, published_date) VALUES (1, 'Book 1', 'Description 1', 1, 10, 10, 'Genre 1', '2022-01-01T00:00:00')",
            "INSERT INTO book_transaction (id, book_id, borrower_email, borrowed_date, due_date, status) VALUES (1, 1, 'test@example.com', '2022-01-01T00:00:00', '2022-02-01T00:00:00', 'BORROWED')"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM book_transaction",
            "DELETE FROM book",
            "DELETE FROM author"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetAllBorrowedBooks() throws Exception {
        MvcResult mvcResult = mvc.perform(get(baseUrl + "/borrowed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(contentAsString);
        ArrayNode contentNode = (ArrayNode) rootNode.get("content");
        List<BorrowedBookResponse> borrowedBookResponses = objectMapper.convertValue(contentNode, new TypeReference<List<BorrowedBookResponse>>() {});

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(borrowedBookResponses.size()).isEqualTo(1);
    }
    @Test
    @Sql(statements = {
            "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1'), (2, 'Author 2', 'Biography 2')",
            "INSERT INTO book (id, title, description, author_id, quantity, available_quantity, genre, published_date) VALUES (1, 'Book 1', 'Description 1', 1, 10, 10, 'Genre 1', '2022-01-01T00:00:00'), (2, 'Book 2', 'Description 2', 2, 20, 20, 'Genre 2', '2022-01-02T00:00:00')",
            "INSERT INTO book_transaction (id, book_id, borrower_email, borrowed_date, due_date, status) VALUES (1, 1, 'test1@example.com', '2022-01-01T00:00:00', '2022-02-01T00:00:00', 'BORROWED'), (2, 2, 'test2@example.com', '2022-01-02T00:00:00', '2022-02-02T00:00:00', 'RETURNED')"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM book_transaction",
            "DELETE FROM book",
            "DELETE FROM author"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetBookBorrowingStatistics() throws Exception {
        MvcResult mvcResult = mvc.perform(get(baseUrl + "/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        Map<String, Object> statistics = objectMapper.readValue(contentAsString, new TypeReference<Map<String, Object>>() {});

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(statistics).isNotEmpty();
        assertThat(statistics).containsKeys("mostBorrowedBooks", "overdueBooks", "totalBorrowedBooks");
    }
}
