package ua.ardas.udemy_ai.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.ardas.udemy_ai.entities.Transaction;
import ua.ardas.udemy_ai.repositories.TransactionRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;

    @Override
    public Optional<Transaction> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Transaction create(Transaction transaction) {
        // Ensure ID is not accidentally set by client
        transaction.setId(null);
        return repository.save(transaction);
    }

    @Override
    public List<Transaction> listAll() {
        return repository.findAll();
    }
}
