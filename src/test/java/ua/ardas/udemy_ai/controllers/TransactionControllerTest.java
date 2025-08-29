package ua.ardas.udemy_ai.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.mapstruct.factory.Mappers;
import ua.ardas.udemy_ai.entities.Transaction;
import ua.ardas.udemy_ai.entities.Transaction.Status;
import ua.ardas.udemy_ai.mappers.TransactionMapper;
import ua.ardas.udemy_ai.models.TransactionDTO;
import ua.ardas.udemy_ai.services.TransactionService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionController.class)
@Import(TransactionControllerTest.MockConfig.class)
class TransactionControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        TransactionService transactionService() {
            return Mockito.mock(TransactionService.class);
        }

        @Bean
        TransactionMapper transactionMapper() {
            return Mappers.getMapper(TransactionMapper.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionService service;

    private Transaction sampleWithId(long id) {
        return Transaction.builder()
                .id(id)
                .amount(new BigDecimal("123.45"))
                .currency("USD")
                .description("Sample")
                .status(Status.PENDING)
                .createdAt(OffsetDateTime.now().minusHours(1))
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/transactions/{id} returns 200 with body when found")
    void getById_found() throws Exception {
        Transaction t = sampleWithId(10L);
        Mockito.when(service.getById(10L)).thenReturn(Optional.of(t));

        mockMvc.perform(get("/api/transactions/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.amount", is(closeTo(123.45, 0.0001))))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.description", is("Sample")));
    }

    @Test
    @DisplayName("GET /api/transactions/{id} returns 404 when not found")
    void getById_notFound() throws Exception {
        Mockito.when(service.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/transactions/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/transactions returns list of transactions")
    void listAll() throws Exception {
        Mockito.when(service.listAll()).thenReturn(List.of(
                sampleWithId(1L),
                sampleWithId(2L)
        ));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("POST /api/transactions creates and returns 201 with Location and body")
    void create_returnsCreated() throws Exception {
        TransactionDTO request = TransactionDTO.builder()
                .amount(new BigDecimal("50.00"))
                .currency("EUR")
                .description("New Transaction")
                .status("PENDING")
                .build();

        Transaction created = Transaction.builder()
                .id(42L)
                .amount(new BigDecimal("50.00"))
                .currency("EUR")
                .description("New Transaction")
                .status(Status.PENDING)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        Mockito.when(service.create(any(Transaction.class))).thenReturn(created);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/transactions/42"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.currency", is("EUR")))
                .andExpect(jsonPath("$.description", is("New Transaction")))
                .andExpect(jsonPath("$.amount", is(closeTo(50.00, 0.0001))));
    }
}
