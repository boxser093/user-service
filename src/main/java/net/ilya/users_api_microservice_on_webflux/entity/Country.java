package net.ilya.users_api_microservice_on_webflux.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("person.countries")
public class Country {
    @Id
    private Long id;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String name;
    private String alpha2;
    private String alpha3;
    private StatusEntity status;

}
