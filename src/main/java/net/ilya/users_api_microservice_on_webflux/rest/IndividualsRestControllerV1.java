package net.ilya.users_api_microservice_on_webflux.rest;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.users_api_microservice_on_webflux.dto.IndividualDto;
import net.ilya.users_api_microservice_on_webflux.mapper.IndividualMapper;
import net.ilya.users_api_microservice_on_webflux.service.IndividualService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Individual controller", description = "Operation with individuals")
@RequestMapping("/api/v1/individuals")
public class IndividualsRestControllerV1 {
    private final IndividualService individualService;
    private final IndividualMapper mapper;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Individual exist",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndividualDto.class))}),
            @ApiResponse(responseCode = "400", description = "Individual not exist by id",
                    content = @Content)})
    @GetMapping(value = "/{id}")
    public Mono<?> getIndividualById(@PathVariable UUID id) {
        return individualService.findById(id).map(mapper::map);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Individual created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndividualDto.class))}),
            @ApiResponse(responseCode = "400", description = "Duplicate User",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Duplicate Individual",
                    content = @Content)})
    @PostMapping(value = "/")
    public Mono<?> registerIndividual(@RequestBody IndividualDto individualDto) {
        return individualService.saveIndividual(individualDto);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Individual verify",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndividualDto.class))}),
            @ApiResponse(responseCode = "400", description = "Individual not exist",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "User not exist",
                    content = @Content)})
    @PutMapping(value = "/verified")
    public Mono<?> verifiedIndividual(@RequestBody IndividualDto individualDto) {
        return individualService.verified(individualDto);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Individual updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndividualDto.class))}),
            @ApiResponse(responseCode = "400", description = "Individual not exist",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "User not exist",
                    content = @Content)})
    @PutMapping(value = "/")
    public Mono<?> updateIndividual(@RequestBody IndividualDto individualDto) {
        return individualService.updateIndividual(individualDto);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Individual deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndividualDto.class))}),
            @ApiResponse(responseCode = "400", description = "Individual not exist by id",
                    content = @Content)})
    @DeleteMapping(value = "/{id}")
    public Mono<?> deleteIndividual(@PathVariable UUID id) {
        return individualService.deletedIndividual(id);

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Individuals exist",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndividualDto.class))}),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = @Content)})
    @GetMapping(value = "/list")
    public Flux<?> findAllIndividuals() {
        return individualService.findAll().map(mapper::map);

    }
}
