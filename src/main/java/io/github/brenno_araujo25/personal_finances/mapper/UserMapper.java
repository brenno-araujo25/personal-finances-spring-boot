package io.github.brenno_araujo25.personal_finances.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import io.github.brenno_araujo25.personal_finances.dto.UserDTO;
import io.github.brenno_araujo25.personal_finances.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(User user);

}
