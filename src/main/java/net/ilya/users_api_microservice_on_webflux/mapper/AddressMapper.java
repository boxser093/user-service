package net.ilya.users_api_microservice_on_webflux.mapper;

import net.ilya.users_api_microservice_on_webflux.dto.AddressDto;
import net.ilya.users_api_microservice_on_webflux.entity.Address;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = CountryMapper.class)
public interface AddressMapper {
    AddressDto map(Address address);

    @InheritInverseConfiguration
    Address map(AddressDto addressDto);

    List<AddressDto> map(List<Address> addresses);
}
