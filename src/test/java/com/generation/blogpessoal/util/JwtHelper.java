package com.generation.blogpessoal.util;

import org.springframework.http.HttpHeaders;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.UsuarioLogin;

public class JwtHelper {

	private JwtHelper() {}
	
	public static String obterToken(TestRestTemplate testeRestTemplate, String usuario, String Senha) {
		
		UsuarioLogin usuarioLogin = TesteBuilder.criarUsuarioLogin(usuario, Senha);
		
		// Create request
		HttpEntity<UsuarioLogin> request = new HttpEntity<UsuarioLogin>(usuarioLogin);
		
		// Make POST request to /usuarios/logar
		ResponseEntity<UsuarioLogin> response = testeRestTemplate
				.exchange("/usuarios/logar", HttpMethod.POST, request, UsuarioLogin.class);
		
		UsuarioLogin corpoResposta = response.getBody();
		
		if (corpoResposta != null && corpoResposta.getToken() != null) {
			return corpoResposta.getToken();
		} 
		
		throw new RuntimeException("Falha no login" + usuario);
		
	}
	
	public static <T> HttpEntity<T> criarRequisicaoComToken(T corpo, String token) {
		HttpHeaders cabecalho = new HttpHeaders();
	    String tokenLimpo = token.startsWith("Bearer ") ? token.substring(7) : token;
	    cabecalho.setBearerAuth(tokenLimpo);
	    return new HttpEntity<>(corpo, cabecalho);
	}
	
	public static HttpEntity<Void> criarRequisicaoComToken(String token) {
		return criarRequisicaoComToken(null, token);
	}
}
