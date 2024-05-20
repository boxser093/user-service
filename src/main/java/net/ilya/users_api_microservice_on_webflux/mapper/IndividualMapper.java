package net.ilya.users_api_microservice_on_webflux.mapper;

import net.ilya.users_api_microservice_on_webflux.dto.IndividualDto;
import net.ilya.users_api_microservice_on_webflux.entity.Individual;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface IndividualMapper {
    IndividualDto map(Individual individual);

    @InheritInverseConfiguration
    Individual map(IndividualDto individualDto);

    List<IndividualDto> map(List<Individual> individual);
}
