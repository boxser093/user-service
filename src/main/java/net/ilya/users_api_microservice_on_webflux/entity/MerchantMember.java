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
@Table("person.merchant_members")
public class MerchantMember {
    @Id
    private UUID id;
    private UUID userId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private UUID merchantId;
    private MemberRole memberRole;
    private StatusEntity status;

    @Transient
    @ToString.Exclude
    private Merchant merchant;

    @Transient
    @ToString.Exclude
    private User user ;
}
