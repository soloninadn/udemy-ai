package ua.ardas.udemy_ai.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.ardas.udemy_ai.entities.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
