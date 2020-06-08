package com.mitocode.service;

import com.mitocode.document.Cliente;

import reactor.core.publisher.Flux;

public interface IClienteService extends ICRUD<Cliente, String> {

	Flux<Cliente> listarDemoradoService();
	
	Flux<Cliente> listarSobreCargadoService();
	
}
