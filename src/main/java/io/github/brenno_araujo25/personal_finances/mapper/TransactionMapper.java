package io.github.brenno_araujo25.personal_finances.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import io.github.brenno_araujo25.personal_finances.dto.TransactionRequest;
import io.github.brenno_araujo25.personal_finances.dto.TransactionResponse;
import io.github.brenno_araujo25.personal_finances.entity.Transaction;
import io.github.brenno_araujo25.personal_finances.entity.TransactionType;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TransactionMapper {

    @Mapping(target = "type", source = "type", qualifiedByName = "enumToString")
    TransactionResponse toResponse(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "type", source = "type", qualifiedByName = "stringToEnum")
    Transaction toEntity(TransactionRequest transactionRequest);

    @Named("stringToEnum")
    default TransactionType stringToEnum(String type) {
        return TransactionType.valueOf(type.toUpperCase());
    }

    @Named("enumToString")
    default String enumToString(TransactionType type) {
        return type.name();
    }

}
