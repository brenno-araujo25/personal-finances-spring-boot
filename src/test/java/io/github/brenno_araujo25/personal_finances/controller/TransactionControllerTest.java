package io.github.brenno_araujo25.personal_finances.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.brenno_araujo25.personal_finances.dto.SummaryResponse;
import io.github.brenno_araujo25.personal_finances.dto.TransactionRequest;
import io.github.brenno_araujo25.personal_finances.dto.TransactionResponse;
import io.github.brenno_araujo25.personal_finances.service.TransactionService;

public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void testCreateTransaction() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest();
        TransactionResponse transactionResponse = new TransactionResponse();

        when(transactionService.createTransaction(any())).thenReturn(transactionResponse);

        mockMvc.perform(post("/transactions")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(transactionRequest)))
            .andExpect(jsonPath("$").exists())
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(transactionResponse)));
    }

    @Test
    void testGetuserTransactions() throws Exception {
        when(transactionService.getUserTransactions()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/transactions"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(status().isOk());
    }

    @Test
    void testGetTransactionById() throws Exception {
        UUID transactionId = UUID.randomUUID();
        TransactionResponse transactionResponse = new TransactionResponse();

        when(transactionService.getTransactionById(transactionId)).thenReturn(transactionResponse);

        mockMvc.perform(get("/transactions/" + transactionId)            
            .contentType("application/json"))
            .andExpect(jsonPath("$").exists())
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(transactionResponse)));
    }

    @Test
    void testUpdateTransaction() throws Exception {
        UUID transactionId = UUID.randomUUID();
        TransactionRequest transactionRequest = new TransactionRequest();
        TransactionResponse transactionResponse = new TransactionResponse();

        when(transactionService.updateTransaction(transactionId, transactionRequest)).thenReturn(transactionResponse);

        mockMvc.perform(put("/transactions/" + transactionId)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(transactionRequest)))
            .andExpect(jsonPath("$").exists())
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(transactionResponse)));
    }

    @Test
    void testDeleteTransaction() throws Exception {
        UUID transactionId = UUID.randomUUID();
        doNothing().when(transactionService).deleteTransaction(transactionId);

        mockMvc.perform(delete("/transactions/" + transactionId))
            .andExpect(status().isNoContent());
    }

    @Test
    void testGetSummary() throws Exception {
        SummaryResponse summaryResponse = new SummaryResponse();

        when(transactionService.getFinancialSummary()).thenReturn(summaryResponse);

        mockMvc.perform(get("/transactions/summary"))
            .andExpect(jsonPath("$").exists())
            .andExpect(status().isOk());
    }

}
