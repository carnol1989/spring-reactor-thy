package com.mitocode.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mitocode.document.Factura;
import com.mitocode.pagination.PageSupport;
import com.mitocode.repo.IFacturaRepo;
import com.mitocode.service.IFacturaService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FacturaServiceImpl implements IFacturaService {

	@Autowired
	private IFacturaRepo repo;

	@Override
	public Mono<Factura> registrarService(Factura t) {
		return repo.save(t);
	}

	@Override
	public Mono<Factura> modificarService(Factura t) {
		return repo.save(t);
	}

	@Override
	public Flux<Factura> listarService() {
		return repo.findAll();
	}

	@Override
	public Mono<Factura> listarPorIdService(String v) {
		return repo.findById(v);
	}

	@Override
	public Mono<Void> eliminarService(String v) {
		return repo.deleteById(v);
	}

	@Override
	public Mono<PageSupport<Factura>> listarPaginaService(Pageable page) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
