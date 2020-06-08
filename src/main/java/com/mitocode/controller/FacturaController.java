package com.mitocode.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.mitocode.service.IClienteService;
import com.mitocode.service.IFacturaService;

@Controller
@SessionAttributes("factura")
@RequestMapping("/facturas")
public class FacturaController {

	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IFacturaService facturaService;
	
}
