package io.github.brenno_araujo25.personal_finances.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import io.github.brenno_araujo25.personal_finances.dto.UserDTO;
import io.github.brenno_araujo25.personal_finances.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    UserDTO toDTO(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserDTO userDTO);

}
