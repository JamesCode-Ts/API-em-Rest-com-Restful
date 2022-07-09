package curso.api.rest.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDTO;
import curso.api.rest.repository.UsuarioRepository;
@CrossOrigin /*libera o acesso a todos os end-pont*/ /* end-pont é um método implementado para regra de negócio*/
@RestController 
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired /*Se fosse CDI seria @Inject*/
	private UsuarioRepository usuarioRepository;
	
	
	
	
	
	/*Serviçp RESTful*/
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> init(@PathVariable (value = "id") Long id) {
			
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		
			return new ResponseEntity<UsuarioDTO>( new UsuarioDTO(usuario.get()) ,HttpStatus.OK);

}
	
	/*Vamos supor que o carregamento de usuário seja um processo lento
	 * e queremos controlar ele com cache para agilizar o processo*/
	@GetMapping(value = "/", produces = "application/json")
	@Cacheable("cacheusuarios")	
	public ResponseEntity<List<Usuario>> usuario () throws InterruptedException{
		
		List<Usuario> list =(List<Usuario>) usuarioRepository.findAll();
		
		Thread.sleep(6000);/*Segura o codigo por 6 segunos simulando um processo lento*/
		
		return new ResponseEntity<List<Usuario>>(list,HttpStatus.OK);
		
		
	}
	

	@PostMapping(value = "/", produces = "application/json") /*O método Post salva no banco de dados*/
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception{ /*Passa o objeto inteiro pela a @RequestBody = converte o jason em um objeto*/
		
		/** Consumindo API publica externa **/
		URL url = new URL("https://viacep.com.br/ws/" +usuario.getCep()+ "/json/");
		URLConnection connection = url.openConnection(); /*Abre a conecção*/
		InputStream is = connection.getInputStream(); /* Vai vim os dados das requisições*/
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8")); /* Prepara a leitura e dentro*/
		
		String cep = "";
		StringBuffer jsonCep = new StringBuffer(); /*Serve para juntar o retorno*/
		
		while((cep = br.readLine()) != null){  /*Enquando tiver linhas, coloca os dados da linha na variavel cep*/
			jsonCep.append(cep); /* Junta as linhas*/
			
		}

		System.err.println(jsonCep.toString());
		/** Consumindo API publica externa **/
		
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		
		
	   usuario.setCep(userAux.getCep());
	   usuario.setLogradouro(userAux.getLogradouro());
	   usuario.setComplemento(userAux.getComplemento());
	   usuario.setBairro(userAux.getBairro());
	   usuario.setLocalidade(userAux.getLocalidade());
	   usuario.setUf(userAux.getBairro());
	
	

		String senhacriptografada = new BCryptPasswordEncoder()
				.encode(usuario.getSenha()); /* Para criptografia de senha */
		usuario.setSenha(senhacriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
		
}
	
	
	
	
	 

	
	@PutMapping(value = "/", produces = "application/json") /*O método Put atualiza no banco de dados. Se não tiver id ele inseriu um e se tiver um id ele altera.*/
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario){ 
		
		
		

		Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());
		
		
		if (!userTemporario.getSenha().equals(usuario.getSenha())) { /*Senhas diferentes*/
			String senhacriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhacriptografada);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);

}
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete (@PathVariable("id") long id) {
		
		usuarioRepository.deleteById(id);
		
		return "ok";
	}
	
}