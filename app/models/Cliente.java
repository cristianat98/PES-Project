package models;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.*;

import play.db.jpa.Blob;

import play.db.jpa.Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;



@Entity
public class Cliente extends Model {

	public String nombre;
	public String mail;
	public String apellido1;
	public String apellido2;
	public String usuario;
	public String contraseña;
	public int admin;
	public Blob perfil;



	public Cliente(String usuario, String contraseña) {

		this.nombre = "";
		this.apellido1="";
		this.apellido2="";
		this.usuario = usuario;
		this.contraseña = contraseña;
		this.admin = 0;
		this.mail ="";
		this.perfil = null;
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

	public String getApellido2(){
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
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
}
