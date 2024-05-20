package net.ilya.users_api_microservice_on_webflux.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericService<T, V> {
    Mono<T> findById(V v);

    Mono<T> create(T t);

    Mono<T> update(T t);

    Mono<T> deleted(V v);
    Flux<T> findAll();

}
