package models;

import java.util.*;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Compra extends Model{
	
	@ManyToOne
	public Cliente cliente;

	@ManyToOne
	public Prenda prenda;

	public Date fecha;
	int cantidad;

	public Compra() {
	}
	
	public Compra(Cliente cliente, Prenda prenda, int cantidad) {
		this.cliente = cliente;
		this.prenda= prenda;
		this.cantidad = cantidad;
		this.fecha = new Date();
	}

	public Cliente getCliente() {
		return cliente;
	}

	public Prenda getPrenda() {
		return prenda;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public void setPrenda(Prenda prenda) {
		this.prenda = prenda;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
}
