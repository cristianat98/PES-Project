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
	public int carrito; //si carrito = 1 se trata de carrito , ci carrito =0 es una compra definitiva

	public Compra() {
	}
	
	public Compra(Cliente cliente, Prenda prenda, Date fecha) {
		this.cliente = cliente;
		this.prenda= prenda;
		this.fecha = fecha;
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

	public int getCarrito() {
		return carrito;
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

	public void setCarrito(int carrito) {
		this.carrito = carrito;
	}
}
