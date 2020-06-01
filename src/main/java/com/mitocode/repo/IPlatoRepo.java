package com.mitocode.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.mitocode.document.Plato;

public interface IPlatoRepo extends ReactiveMongoRepository<Plato, String> {

}
