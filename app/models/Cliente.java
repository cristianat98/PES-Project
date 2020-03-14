package models;

import javax.persistence.Entity;
import play.db.jpa.Model;



@Entity
public class Cliente extends Model {
	String nombre;
	String nombreusuario;
	String password;
	int numcompras;

	public Cliente(String nombre, String nombreusuario, String password) {
		super();
		this.nombre = nombre;
		this.nombreusuario = nombreusuario;
		this.password = password;
		this.numcompras =0;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombreusuario() {
		return nombreusuario;
	}

	public void setNombreusuario(String nombreusuario) {
		this.nombreusuario = nombreusuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getNumcompras() {
		return numcompras;
	}

	public void setNumcompras(int numcompras) {
		this.numcompras = numcompras;
	}

}
