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
@Table("person.merchant_members_invitations")
public class MerchantMemberInvitations {
    @Id
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime expires;
    private UUID merchantId;
    private String firstName;
    private String lastName;
    private String email;
    private StatusEntity status;

    @Transient
    @ToString.Exclude
    private Merchant merchant;
}
