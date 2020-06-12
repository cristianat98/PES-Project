package models;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;
import java.util.*;



@Entity
public class Cliente extends Model {

	public String nombre;
	public String mail;
	public String apellido1;
	public String apellido2;
	public String direccion;
	public String usuario;
	public String contraseña;
	public int cuentaBancaria;
	public int admin;



	public Cliente(String usuario, String contraseña) {
		//this.nombre = nombre;
		//this.apellido1=apellido1;
		//this.apellido2=apellido2;
		//this.direccion=direccion;
		this.usuario = usuario;
		this.contraseña = contraseña;
		this.admin = 0;
	}

	public String getNombre() {
		return nombre;
	}

	public int getAdmin()
	{
		return this.admin;
	}

	public void setAdmin(int admin)
	{
		this.admin = admin;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
		
	}

	public String getApellido1() {
		return apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public String getApellido2()
	{
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getContraseña() {
		return contraseña;
	}

	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}

	public int getCuentaBancaria() {
		return cuentaBancaria;
	}

	public void setCuentaBancaria(int cuentaBancaria) {
		this.cuentaBancaria = cuentaBancaria;
	}
	



}
