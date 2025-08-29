package ua.ardas.udemy_ai.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.ardas.udemy_ai.entities.Transaction;
import ua.ardas.udemy_ai.services.TransactionService;

import java.net.URI;
import java.util.List;

/**
 * REST controller exposing CRUD operations for {@link Transaction} resources.
 *
 * Base path: /api/transactions
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    /**
     * Retrieve a transaction by its identifier.
     *
     * @param id the transaction ID from the path
     * @return 200 OK with the {@link Transaction} in the body when found; 404 Not Found otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * List all transactions.
     *
     * @return 200 OK with a JSON array of {@link Transaction} objects
     */
    @GetMapping
    public List<Transaction> listAll() {
        return service.listAll();
    }

    /**
     * Create a new transaction.
     *
     * Notes:
     * - If status is omitted, it defaults to PENDING (handled by entity lifecycle).
     * - The Location header of the response points to the created resource.
     *
     * @param transaction the transaction payload from request body
     * @return 201 Created with Location header and the created {@link Transaction} in the body
     */
    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
        Transaction created = service.create(transaction);
        return ResponseEntity.created(URI.create("/api/transactions/" + created.getId()))
                .body(created);
    }
}
