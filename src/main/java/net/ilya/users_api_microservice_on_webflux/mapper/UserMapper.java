package net.ilya.users_api_microservice_on_webflux.mapper;


import net.ilya.users_api_microservice_on_webflux.dto.UserDto;
import net.ilya.users_api_microservice_on_webflux.entity.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface UserMapper {
    UserDto map(User user);

    @InheritInverseConfiguration
    User map(UserDto userDto);

    List<UserDto> map(List<User> users);
}
