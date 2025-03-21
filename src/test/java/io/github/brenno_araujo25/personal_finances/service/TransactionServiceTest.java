package io.github.brenno_araujo25.personal_finances.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import io.github.brenno_araujo25.personal_finances.dto.SummaryResponse;
import io.github.brenno_araujo25.personal_finances.dto.TransactionRequest;
import io.github.brenno_araujo25.personal_finances.dto.TransactionResponse;
import io.github.brenno_araujo25.personal_finances.entity.Transaction;
import io.github.brenno_araujo25.personal_finances.entity.TransactionType;
import io.github.brenno_araujo25.personal_finances.entity.User;
import io.github.brenno_araujo25.personal_finances.mapper.TransactionMapper;
import io.github.brenno_araujo25.personal_finances.repository.TransactionRepository;
import io.github.brenno_araujo25.personal_finances.repository.UserRepository;
import io.github.brenno_araujo25.personal_finances.exception.InvalidTransactionException;
import io.github.brenno_araujo25.personal_finances.exception.TransactionNotFoundException;

public class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    private User mockUser;
    private Transaction mockTransaction;
    private TransactionRequest mockRequest;
    private TransactionResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setUsername("brenno");

        mockTransaction = new Transaction();
        mockTransaction.setId(UUID.randomUUID());
        mockTransaction.setAmount(BigDecimal.valueOf(1000));
        mockTransaction.setUser(mockUser);
        mockTransaction.setType(TransactionType.INCOME);
        mockTransaction.setDescription("description");
        mockTransaction.setCategory("salary");

        mockRequest = new TransactionRequest();
        mockRequest.setAmount(BigDecimal.valueOf(1000));
        mockRequest.setDescription("description");
        mockRequest.setType(String.valueOf(TransactionType.INCOME));
        mockRequest.setCategory("salary");

        mockResponse = new TransactionResponse();
        mockResponse.setId(mockTransaction.getId());
        mockResponse.setAmount(mockTransaction.getAmount());
        mockResponse.setDescription(mockTransaction.getDescription());
        mockResponse.setType(String.valueOf(mockTransaction.getType()));
        mockResponse.setCategory(mockTransaction.getCategory());

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("brenno");
        SecurityContextHolder.setContext(securityContext);

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveTransaction() {
        when(userRepository.findByUsername("brenno")).thenReturn(Optional.of(mockUser));
        when(transactionMapper.toEntity(mockRequest)).thenReturn(mockTransaction);
        when(transactionMapper.toResponse(mockTransaction)).thenReturn(mockResponse);
        when(transactionRepository.save(mockTransaction)).thenReturn(mockTransaction);

        TransactionResponse response = transactionService.createTransaction(mockRequest);

        assertNotNull(response);
        assertEquals(mockResponse.getId(), response.getId());
        assertEquals(mockResponse.getAmount(), response.getAmount());
        assertEquals(mockResponse.getDescription(), response.getDescription());
        assertEquals(mockResponse.getType(), response.getType());
        assertEquals(mockResponse.getCategory(), response.getCategory());
        verify(transactionRepository).save(mockTransaction);
    }

    @Test
    void shouldThrowExceptionWhenAmountIsZeroOrNegative() {
        when(userRepository.findByUsername("brenno")).thenReturn(Optional.of(mockUser));
        mockRequest.setAmount(BigDecimal.valueOf(-1000));

        assertThrows(InvalidTransactionException.class, () -> {
            transactionService.createTransaction(mockRequest);
        });
    }

    @Test
    void shouldReturnUserTransactions() {
        when(userRepository.findByUsername("brenno")).thenReturn(Optional.of(mockUser));
        when(transactionRepository.findByUser(mockUser)).thenReturn(List.of(mockTransaction));
        when(transactionMapper.toResponse(mockTransaction)).thenReturn(mockResponse);

        List<TransactionResponse> response = transactionService.getUserTransactions();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(mockResponse.getId(), response.get(0).getId());
    }

    @Test
    void shouldReturnTransactionById() {
        when(transactionRepository.findById(mockTransaction.getId())).thenReturn(Optional.of(mockTransaction));
        when(transactionMapper.toResponse(mockTransaction)).thenReturn(mockResponse);

        TransactionResponse response = transactionService.getTransactionById(mockTransaction.getId());

        assertNotNull(response);
        assertEquals(mockResponse.getId(), response.getId());
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {
        UUID transactionId = UUID.randomUUID();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.getTransactionById(transactionId);
        });
    }

    @Test
    void shouldDeleteTransaction() {
        when(transactionRepository.findById(mockTransaction.getId())).thenReturn(Optional.of(mockTransaction));
        doNothing().when(transactionRepository).delete(mockTransaction);
        
        transactionService.deleteTransaction(mockTransaction.getId());

        verify(transactionRepository).delete(mockTransaction);
    }

    @Test
    void shouldThrowExceptionWhenTransactionToDeleteNotFound() {
        UUID transactionId = UUID.randomUUID();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.deleteTransaction(transactionId);
        });
    }

    @Test
    void shouldReturnFinancialSummary() {
        when(userRepository.findByUsername("brenno")).thenReturn(Optional.of(mockUser));
        when(transactionRepository.findByUser(mockUser)).thenReturn(List.of(mockTransaction));

        SummaryResponse summary = transactionService.getFinancialSummary();

        assertNotNull(summary);
        assertEquals(BigDecimal.valueOf(1000), summary.getTotalIncome());
        assertEquals(BigDecimal.ZERO, summary.getTotalExpense());
        assertEquals(BigDecimal.valueOf(1000), summary.getTotalBalance());
    }
}
