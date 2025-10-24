package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.JwtHelper;
import com.generation.blogpessoal.util.TesteBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testeRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private static final String URL_BASE = "/usuarios";
	private static final String USUARIO = "root@root.com";
	private static final String SENHA = "rootroot";
	
	@BeforeAll
	void inicio() {
		usuarioRepository.deleteAll();
		usuarioService.cadastrarUsuario(TesteBuilder.criarUsuario(null, "Root", USUARIO, SENHA));
	}

	@Test
	@DisplayName("1 - Deve cadastrar um novo usuário com sucesso")
	void deveCadastrarUsuario() {
		//Given
		Usuario usuario = TesteBuilder.criarUsuario(null, "Thuany", "thuany@email.com.br", "12345678");
		
		//When	
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testeRestTemplate.exchange(URL_BASE + "/cadastrar", HttpMethod.POST,
				requisicao, Usuario.class);
		
		//Then
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("2 - Não deve cadastrar usuário duplicado")
	void naoDeveCadastrarUsuario() {
		//Given
		Usuario usuario = TesteBuilder.criarUsuario(null, "Mila", "mila@email.com.br", "1234567894");
		usuarioService.cadastrarUsuario(usuario);
		
		//When	
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testeRestTemplate.exchange(URL_BASE + "/cadastrar", HttpMethod.POST,
				requisicao, Usuario.class);
		
		//Then
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		//assertNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("3 - Deve atualizar os dados do usuário com sucesso")
	void DeveAtualizarUmUsuario() {
		//Given
		Usuario usuario = TesteBuilder.criarUsuario(null, "Myriam", "myriam@email.com.br", "1234567894");
		Optional<Usuario> usuarioCadastrado =  usuarioService.cadastrarUsuario(usuario);
		
		Usuario usuarioUpdate = TesteBuilder.criarUsuario(usuarioCadastrado.get().getId(),"Myriam Silva", "myriam@email.com", "987654321"); 
		
		//When
		
		//Gerar o Token
		String token = JwtHelper.obterToken(testeRestTemplate, USUARIO, SENHA);
		
		//Criar a requisição com o token
		HttpEntity<Usuario> requisicao = JwtHelper.criarRequisicaoComToken(usuarioUpdate, token);
		
		//Fazer a requisição para atualizar
		ResponseEntity<Usuario> resposta = testeRestTemplate.exchange(URL_BASE + "/atualizar", HttpMethod.PUT,
				requisicao, Usuario.class);
		
		//Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	//Listar todas
	@Test
	@DisplayName("4 - Deve listar todos os usuários com sucesso")
	void deveListarTodosUsuarios() {
		
		// Given
		usuarioService.cadastrarUsuario(TesteBuilder.criarUsuario(null, "Ana Marques", 
				"ana_marques@email.com.br", "12345678"));
		usuarioService.cadastrarUsuario(TesteBuilder.criarUsuario(null, "Carlos Moura", 
				"carlos_moura@email.com.br", "12345678"));
		
		// When
		String token = JwtHelper.obterToken(testeRestTemplate, USUARIO, SENHA);
		HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
		ResponseEntity<Usuario[]> resposta = testeRestTemplate.exchange(
				URL_BASE + "/all", HttpMethod.GET, requisicao, Usuario[].class);
		
		// Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}

	
	// Listar por ID
	 @Test
	 @DisplayName("5 - Deve listar o usuário por ID com sucesso")
	 void deveListarUsuarioPorId() {
		 
		// Given
	    Optional<Usuario> usuarioCriado = usuarioService.cadastrarUsuario(
	    TesteBuilder.criarUsuario(null, "Mariana Silva", "mariana_silva@email.com.br", "12345678")
	    );

	    Long id = usuarioCriado.get().getId();

	    String token = JwtHelper.obterToken(testeRestTemplate, USUARIO, SENHA);
	    HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
	    ResponseEntity<Usuario> resposta = testeRestTemplate.exchange(
	        URL_BASE + "/" + id, HttpMethod.GET, requisicao, Usuario.class
	    );

	    assertEquals(HttpStatus.OK, resposta.getStatusCode());
	    assertNotNull(resposta.getBody());
	    assertEquals(usuarioCriado.get().getId(), resposta.getBody().getId());
	    }

}
