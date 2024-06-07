package net.ilya.users_api_microservice_on_webflux.mapper;


import net.ilya.users_api_microservice_on_webflux.dto.MerchantDto;
import net.ilya.users_api_microservice_on_webflux.entity.Merchant;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface MerchantMapper {
    MerchantDto map(Merchant merchant);

    @InheritInverseConfiguration
    Merchant map(MerchantDto merchantDto);

    List<MerchantDto> map(List<Merchant> merchant);
}
