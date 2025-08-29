package ua.ardas.udemy_ai.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.ardas.udemy_ai.entities.Transaction;
import ua.ardas.udemy_ai.mappers.TransactionMapper;
import ua.ardas.udemy_ai.models.TransactionDTO;
import ua.ardas.udemy_ai.services.TransactionService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
    private final TransactionMapper mapper;

    /**
     * Retrieve a transaction by its identifier.
     *
     * @param id the transaction ID from the path
     * @return 200 OK with the TransactionDTO in the body when found; 404 Not Found otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * List all transactions.
     *
     * @return 200 OK with a JSON array of TransactionDTO objects
     */
    @GetMapping
    public List<TransactionDTO> listAll() {
        return service.listAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new transaction.
     *
     * Notes:
     * - If status is omitted, it defaults to PENDING (handled by entity lifecycle).
     * - The Location header of the response points to the created resource.
     *
     * @param dto the transaction payload from request body
     * @return 201 Created with Location header and the created TransactionDTO in the body
     */
    @PostMapping
    public ResponseEntity<TransactionDTO> create(@RequestBody TransactionDTO dto) {
        Transaction toCreate = mapper.toEntity(dto);
        Transaction created = service.create(toCreate);
        TransactionDTO createdDto = mapper.toDto(created);
        return ResponseEntity.created(URI.create("/api/transactions/" + created.getId()))
                .body(createdDto);
    }
}
