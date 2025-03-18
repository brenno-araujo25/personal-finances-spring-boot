package io.github.brenno_araujo25.personal_finances.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.brenno_araujo25.personal_finances.mapper.UserMapper;

@Configuration
public class MapStructConfig {

    @Bean
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

}
