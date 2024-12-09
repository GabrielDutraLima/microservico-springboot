package com.suporte.microservico.microservico_springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MicroservicoSpringBootApplicationTests {

	@Autowired
	private ApplicationContext context;

	// Teste para garantir que o contexto da aplicação está carregado corretamente
	@Test
	void contextLoads() {
		assertNotNull(context, "O contexto da aplicação deveria ter sido carregado");
	}
}
