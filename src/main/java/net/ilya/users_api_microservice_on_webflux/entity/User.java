package net.ilya.users_api_microservice_on_webflux.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table("person.users")
public class User {
    @Id
    private UUID id;
    private String secretKey;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String firstName;
    private String lastName;
    private LocalDateTime verifiedAt;
    private LocalDateTime archivedAt;
    private StatusEntity status;
    private boolean filled;
    private UUID addressId;

    @Transient
    @ToString.Exclude
    private Address address;
}
