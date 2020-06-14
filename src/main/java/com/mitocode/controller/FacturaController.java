package com.mitocode.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.mitocode.document.Cliente;
import com.mitocode.document.Factura;
import com.mitocode.document.FacturaItem;
import com.mitocode.document.Plato;
import com.mitocode.pagination.PageSupport;
import com.mitocode.service.IClienteService;
import com.mitocode.service.IFacturaService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@SessionAttributes("factura")
@RequestMapping("/facturas")
public class FacturaController {

	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IFacturaService facturaService;
	
	@GetMapping(value = "/listar")
	public Mono<String> listarPageableController(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
		int pageSize = 5;
		
		Pageable pageRequest = PageRequest.of(page, pageSize);
		
		Mono<PageSupport<Cliente>> clienteMono = clienteService.listarPaginaService(pageRequest);
		
		model.addAttribute("pageSp", clienteMono);
		model.addAttribute("url", "/facturas/listar");
		
		return Mono.just("facturas/listar");
	}
	
	@GetMapping("/form/{idCliente}")
	public Mono<String> nuevaFacturaController(@PathVariable(value = "idCliente") String idCliente, Model model) {
		return clienteService.listarPorIdService(idCliente)
				.defaultIfEmpty(new Cliente())
				.flatMap(c -> {
					if (c.getId() == null) {
						return Mono.error(new InterruptedException("No existe el cliente"));
					}
					return Mono.just(c);
				}).doOnNext(c -> {
					Factura factura = new Factura();
					factura.setCliente(c);
					model.addAttribute("factura", factura);
					model.addAttribute("titulo", "Nueva Factura");
				}).then(Mono.just("facturas/form")
						.onErrorResume(ex -> Mono.just("redirect:/facturas/listar?error=No+existe+cliente")));
	}
	
	@GetMapping("/operar")
	public Mono<String> guardarController(Factura factura, Model model, 
			@RequestParam(value = "clienteId") String clienteId, 
			@RequestParam(value = "item_id[]", name = "item_id[]", required = false) String[] itemId, 
			@RequestParam(value = "quantity[]", name = "quantity[]", required = false) Integer[] quantity, 
			SessionStatus status) {
		Cliente cliente = new Cliente();
		cliente.setId(clienteId);
		
		factura.setCliente(cliente);
		factura.setCreadoEn(LocalDateTime.now());		
		List<FacturaItem> items = new ArrayList<>();
		for (int i = 0; i < itemId.length; i++) {
			FacturaItem item = new FacturaItem();
			item.setCantidad(quantity[i]);
			item.setPlato(new Plato(itemId[i]));
			items.add(item);
		}
		factura.setItems(items);
		
		status.setComplete();
		model.addAttribute("success", "Factura Generada");
		
		return facturaService.registrarService(factura)
				.then(Mono.just("redirect:/facturas/listar?exito=se+registro+correctamente"));
	}
	
	@GetMapping("/detalle/listar")
	public Mono<String> detalleListarFacturaController(Model model) {
		Flux<Factura> fluxFacturas = facturaService.listarService();
		model.addAttribute("facturas", fluxFacturas);
		model.addAttribute("titulo", "Listar Detalle Factura");
		return Mono.just("facturas/detalle-listar");
	}
	
	@GetMapping("/detalle/{id}")
	public Mono<String> detalleFacturaController(@PathVariable(value = "id") String facturaId, Model model) {
		return facturaService.listarPorIdService(facturaId)
				.doOnNext(f -> {
					model.addAttribute("factura", f);
					model.addAttribute("titulo", "Ver Detalle Factura");
				}).then(Mono.just("facturas/detalle"));
	}
	
}
