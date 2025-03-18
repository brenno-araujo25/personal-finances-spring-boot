package io.github.brenno_araujo25.personal_finances.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.brenno_araujo25.personal_finances.entity.Transaction;
import io.github.brenno_araujo25.personal_finances.entity.User;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUser(User user);
}
