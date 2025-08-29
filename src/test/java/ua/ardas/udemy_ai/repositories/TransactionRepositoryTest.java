package ua.ardas.udemy_ai.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ua.ardas.udemy_ai.entities.Transaction;
import ua.ardas.udemy_ai.entities.Transaction.Status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repository;

    private Transaction buildSample() {
        return Transaction.builder()
                .amount(new BigDecimal("123.4500"))
                .currency("USD")
                .description("Initial payment")
                .status(Status.PENDING) // can be omitted due to @PrePersist default
                .build();
    }

    @Test
    @DisplayName("CRUD: create, read, update, delete Transaction")
    void crudOperations() {
        // Create & Save
        Transaction toSave = buildSample();
        Transaction saved = repository.save(toSave);

        assertThat(saved.getId()).as("ID should be generated").isNotNull();
        assertThat(saved.getAmount()).isEqualByComparingTo("123.4500");
        assertThat(saved.getCurrency()).isEqualTo("USD");
        assertThat(saved.getDescription()).isEqualTo("Initial payment");
        assertThat(saved.getStatus()).isEqualTo(Status.PENDING);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        // Read by ID
        Optional<Transaction> byId = repository.findById(saved.getId());
        assertThat(byId).isPresent();
        Transaction found = byId.get();
        assertThat(found.getAmount()).isEqualByComparingTo("123.4500");
        assertThat(found.getCurrency()).isEqualTo("USD");

        // Find all
        List<Transaction> all = repository.findAll();
        assertThat(all).hasSize(1);

        // Update
        found.setDescription("Updated description");
        found.setStatus(Status.COMPLETED);
        repository.save(found);

        Transaction updated = repository.findById(found.getId()).orElseThrow();
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getStatus()).isEqualTo(Status.COMPLETED);
        assertThat(updated.getUpdatedAt()).isNotNull();

        // Delete
        repository.deleteById(updated.getId());
        assertThat(repository.findAll()).isEmpty();
    }
}
