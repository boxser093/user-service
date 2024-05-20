package net.ilya.users_api_microservice_on_webflux.mapper;

import net.ilya.users_api_microservice_on_webflux.dto.CountryDto;
import net.ilya.users_api_microservice_on_webflux.entity.Country;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;


import java.util.List;
@Mapper(componentModel = "spring")
public interface CountryMapper {
    CountryDto map(Country country);

    @InheritInverseConfiguration
    Country map(CountryDto countryDto);

    List<CountryDto> map(List<Country> countries);
}
