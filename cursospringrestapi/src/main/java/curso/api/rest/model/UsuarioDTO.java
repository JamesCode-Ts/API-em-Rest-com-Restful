package curso.api.rest.model;

import java.io.Serializable;

public class UsuarioDTO implements Serializable{
	
	/*Esta classe serva para omitir dados por questão de segurança.
	 * E só passar para o front os dados que eu quero que sejam mostrados*/
	
	private String userLogin;
	private String userNome;
	private String Cpf;
	
	
	
	public UsuarioDTO(Usuario usuario) {
		
		
		this.userLogin = usuario.getLogin();
		this.userNome = usuario.getNome();
		this.Cpf = usuario.getCpf();
	}
	
	
	
	public String getUserLogin() {
		return userLogin;
	}
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	public String getUserNome() {
		return userNome;
	}
	public void setUserNome(String userNome) {
		this.userNome = userNome;
	}
	public String getCpf() {
		return Cpf;
	}
	public void setCpf(String cpf) {
		Cpf = cpf;
	}

}
