package io.github.brenno_araujo25.personal_finances.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.brenno_araujo25.personal_finances.dto.TransactionRequest;
import io.github.brenno_araujo25.personal_finances.dto.TransactionResponse;
import io.github.brenno_araujo25.personal_finances.entity.Transaction;
import io.github.brenno_araujo25.personal_finances.entity.User;
import io.github.brenno_araujo25.personal_finances.exception.InvalidTransactionException;
import io.github.brenno_araujo25.personal_finances.exception.TransactionNotFoundException;
import io.github.brenno_araujo25.personal_finances.mapper.TransactionMapper;
import io.github.brenno_araujo25.personal_finances.repository.TransactionRepository;
import io.github.brenno_araujo25.personal_finances.repository.UserRepository;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(
        UserRepository userRepository,
        TransactionRepository transactionRepository,
        TransactionMapper transactionMapper
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        User user = getAuthenticatedUser();

        if (transactionRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be greater than zero");
        }

        Transaction transaction = transactionMapper.toEntity(transactionRequest);
        transaction.setUser(user);

        transactionRepository.save(transaction);

        return transactionMapper.toResponse(transaction);
    }

    public List<TransactionResponse> getUserTransactions() {
        User user = getAuthenticatedUser();
        List<Transaction> transactions = transactionRepository.findByUser(user);
        return transactions.stream()
            .map(transactionMapper::toResponse)
            .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionById(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        return transactionMapper.toResponse(transaction);
    }

    public TransactionResponse updateTransaction(UUID id, TransactionRequest transactionRequest) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        if (transactionRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount must be greater than zero");
        }

        transaction.setAmount(transactionRequest.getAmount());
        transaction.setCategory(transactionRequest.getCategory());
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setDateTime(transactionRequest.getDateTime());
        transaction.setType(transactionMapper.stringToEnum(transactionRequest.getType()));

        transactionRepository.save(transaction);

        return transactionMapper.toResponse(transaction);
    }

    public void deleteTransaction(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        transactionRepository.delete(transaction);
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
