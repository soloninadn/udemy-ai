package ua.ardas.udemy_ai.mappers;

import org.mapstruct.Mapper;
import ua.ardas.udemy_ai.entities.Transaction;
import ua.ardas.udemy_ai.models.TransactionDTO;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    // Entity -> DTO
    TransactionDTO toDto(Transaction entity);

    // DTO -> Entity
    Transaction toEntity(TransactionDTO dto);

    // Helper mappings for status enum <-> string
    default String mapStatus(Transaction.Status status) {
        return status != null ? status.name() : null;
    }

    default Transaction.Status mapStatus(String status) {
        return status != null ? Transaction.Status.valueOf(status) : null;
    }
}
