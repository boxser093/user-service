package net.ilya.users_api_microservice_on_webflux.mapper;

import net.ilya.users_api_microservice_on_webflux.dto.MerchantDto;
import net.ilya.users_api_microservice_on_webflux.dto.MerchantMemberDto;
import net.ilya.users_api_microservice_on_webflux.entity.Merchant;
import net.ilya.users_api_microservice_on_webflux.entity.MerchantMember;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, MerchantMapper.class})
public interface MerchantMemberMapper {
    MerchantMemberDto map(MerchantMember merchantMember);

    @InheritInverseConfiguration
    MerchantMember map(MerchantMemberDto merchantMemberDto);

    List<MerchantMemberDto> map(List<MerchantMember> merchantMember);
}
