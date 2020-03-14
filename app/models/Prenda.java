package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Prenda extends Model {
	String tipo;
	String equipo;
	String talla;
	
	@ManyToOne
	public Cliente cliente;
	
	public Prenda(String tipo, String equipo, String talla) {
		super();
		this.tipo = tipo;
		this.equipo = equipo;
		this.talla = talla;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getEquipo() {
		return equipo;
	}

	public void setEquipo(String equipo) {
		this.equipo = equipo;
	}

	public String getTalla() {
		return talla;
	}

	public void setTalla(String talla) {
		this.talla = talla;
	}


}
