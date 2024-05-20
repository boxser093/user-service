package net.ilya.users_api_microservice_on_webflux.utill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.ilya.users_api_microservice_on_webflux.dto.IndividualDto;
import net.ilya.users_api_microservice_on_webflux.entity.Individual;
import net.ilya.users_api_microservice_on_webflux.entity.Merchant;
import net.ilya.users_api_microservice_on_webflux.entity.MerchantMember;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JsonParserCustom {


    public String toJasonJacksonIndividual(IndividualDto o)  {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public String toJasonJacksonMerchant(Merchant o)  {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public String toJasonJacksonMerchantMember(MerchantMember o)  {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
