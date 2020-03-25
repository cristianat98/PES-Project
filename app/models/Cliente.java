package models;

import javax.persistence.Entity;
import play.db.jpa.Model;
import java.util.ArrayList;



@Entity
public class Cliente extends Model {


	public String nombre;
	public String apellido1;
	public String apellido2;
	public String direccion;
	public String usuario;
	public String contraseña;
	public int cuentaBancaria;
	//List <Prenda> carro;

	public Cliente(String usuario, String contraseña)  {
		super();
		//this.nombre = nombre;
		//this.apellido1=apellido1;
		//this.apellido2=apellido2;
		//this.direccion=direccion;
		this.usuario=usuario;
		this.contraseña=contraseña;
		//this.cuentaBancaria=cuentaBancaria;
	
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
		this.save();
	}

	public String getApellido1() {
		return apellido1;
		
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
		this.save();
	}

	public String getApellido2() {
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
		this.save();
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
		this.save();
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
		this.save();
	}

	public String getContraseña() {
		return contraseña;
	}

	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
		this.save();
	}

	public int getCuentaBancaria() {
		return cuentaBancaria;
	}

	public void setCuentaBancaria(int cuentaBancaria) {
		this.cuentaBancaria = cuentaBancaria;
		this.save();
	}
	



}
