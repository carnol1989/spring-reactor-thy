package com.mitocode.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import reactor.core.publisher.Mono;

@Controller
@RequestMapping
public class LoginController {

	@GetMapping(value = { "/login", "/"})
	public Mono<String> loginController(Model model, Principal principal) {
		if (principal != null) {
			return Mono.just("redirect:/clientes/listar");
		}
		return Mono.just("login");
	}
	
}
