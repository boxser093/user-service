package net.ilya.users_api_microservice_on_webflux.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("person.addresses")
public class Address {
    @Id
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Long countryId;
    private String address;
    private String zipCode;
    private LocalDateTime archived;
    private String city;
    private String state;

    @Transient
    @ToString.Exclude
    private Country country;
}
