package ua.ardas.udemy_ai.services;

import ua.ardas.udemy_ai.entities.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    Optional<Transaction> getById(Long id);

    Transaction create(Transaction transaction);

    List<Transaction> listAll();
}
