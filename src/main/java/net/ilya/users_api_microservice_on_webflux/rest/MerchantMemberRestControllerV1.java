package net.ilya.users_api_microservice_on_webflux.rest;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.users_api_microservice_on_webflux.dto.IndividualDto;
import net.ilya.users_api_microservice_on_webflux.dto.MerchantMemberDto;
import net.ilya.users_api_microservice_on_webflux.entity.MerchantMember;
import net.ilya.users_api_microservice_on_webflux.mapper.MerchantMemberMapper;
import net.ilya.users_api_microservice_on_webflux.service.MerchantMemberService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Merchant member controller", description = "Operation with merchant members")
@RequestMapping("/api/v1/members")
public class MerchantMemberRestControllerV1 {
    private final MerchantMemberService merchantMemberService;
    private final MerchantMemberMapper mapper;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member exist",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MerchantMemberDto.class))}),
            @ApiResponse(responseCode = "400", description = "Member not exist by id",
                    content = @Content)})
    @GetMapping(value = "/{id}")
    public Mono<?> getMerchantMemberById(@PathVariable UUID id) {
        return merchantMemberService.findById(id).map(mapper::map);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MerchantMemberDto.class))}),
            @ApiResponse(responseCode = "400", description = "Duplicate user",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invocation has expired",
                    content = @Content)})
    @PostMapping(value = "/{id}")
    public Mono<?> registerMerchantMember(@PathVariable UUID id) {
        return merchantMemberService.createNewMerchantMember(id).map(mapper::map);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MerchantMemberDto.class))}),
            @ApiResponse(responseCode = "400", description = "Member not exist by id",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "User not exist by id",
                    content = @Content)})
    @PutMapping(value = "/")
    public Mono<?> updateMerchantMember(@RequestBody MerchantMemberDto merchantMemberDto) {
        return merchantMemberService.update(mapper.map(merchantMemberDto)).map(mapper::map);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MerchantMemberDto.class))}),
            @ApiResponse(responseCode = "400", description = "Member not exist by id",
                    content = @Content)})
    @DeleteMapping(value = "/{id}")
    public Mono<?> deleteMerchantMember(@PathVariable UUID id) {
        return merchantMemberService.deleted(id).map(mapper::map);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members exist",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndividualDto.class))})})
    @GetMapping(value = "/list")
    public Flux<?> findAllMerchantMembers() {
        return merchantMemberService.findAll().map(mapper::map);

    }

}
