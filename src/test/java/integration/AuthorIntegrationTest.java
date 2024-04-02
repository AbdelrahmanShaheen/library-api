package integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.onesolution.library.LibraryApplication;
import com.onesolution.library.dto.AuthorRequest;
import com.onesolution.library.dto.AuthorResponse;
import com.onesolution.library.dto.BookResponse;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK ,classes = LibraryApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application.properties")
public class AuthorIntegrationTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final String baseUrl = "/api/authors";
    @Test
    public void testCreateAuthor() throws Exception {
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Author Name");
        authorRequest.setBiography("Author Biography");

        mvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andExpect(status().isCreated());
    }
    @Test
    @Sql(statements = "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1'), (2, 'Author 2', 'Biography 2')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM author", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetAllAuthors() throws Exception {
        MvcResult mvcResult = mvc.perform(get(baseUrl))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(contentAsString);
        ArrayNode contentNode = (ArrayNode) rootNode.get("content");
        List<AuthorResponse> authorResponses = objectMapper.convertValue(contentNode, new TypeReference<List<AuthorResponse>>() {});
         assertThat(authorResponses.size()).isEqualTo(2);
    }
    @Test
    @Sql(statements = "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM author", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetAuthorById() throws Exception {
        MvcResult mvcResult = mvc.perform(get(baseUrl + "/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        AuthorResponse authorResponse = objectMapper.readValue(contentAsString, AuthorResponse.class);

        assertThat(authorResponse.getName()).isEqualTo("Author 1");
        assertThat(authorResponse.getBiography()).isEqualTo("Biography 1");
    }
    @Test
    @Sql(statements = "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM author", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdateAuthor() throws Exception {
        AuthorRequest authorRequest = new AuthorRequest();
        authorRequest.setName("Updated Author");
        authorRequest.setBiography("Updated Biography");

        MvcResult mvcResult = mvc.perform(put(baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorRequest)))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(200);
    }
    @Test
    @Sql(statements = "INSERT INTO author (id, name, biography) VALUES (1, 'Author 1', 'Biography 1')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM author", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testDeleteAuthor() throws Exception {
        MvcResult mvcResult = mvc.perform(delete(baseUrl + "/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(204);
    }
    @Test
    @Sql(statements = {
            "INSERT INTO author (name, biography) VALUES ('Author 1', 'Biography 1')",
            "INSERT INTO book (title, description, author_id, quantity, available_quantity, genre, published_date) VALUES ('Book 1', 'Description 1', 1, 10, 10, 'Genre 1', '2022-01-01T00:00:00'), ('Book 2', 'Description 2', 1, 10, 10, 'Genre 2', '2022-01-01T00:00:00')"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = {
            "DELETE FROM book",
            "DELETE FROM author"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testGetBooksByAuthor() throws Exception {
        MvcResult mvcResult = mvc.perform(get(baseUrl + "/1/books"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(contentAsString);
        ArrayNode contentNode = (ArrayNode) rootNode.get("content");
        List<BookResponse> bookResponses = objectMapper.convertValue(contentNode, new TypeReference<List<BookResponse>>() {});

        assertThat(bookResponses.size()).isEqualTo(2);
        assertThat(bookResponses.get(0).getTitle()).isEqualTo("Book 1");
        assertThat(bookResponses.get(1).getTitle()).isEqualTo("Book 2");
    }
}
