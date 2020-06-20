package models;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.db.jpa.*;

@Entity
public class Prenda extends Model {

	public String tipo;
	public String equipo;
	public String talla;
	public int año;
	public int cantidadVendida;
	public int cantidadStock;
	public double precio;
	public Blob imagen;
	
	public Prenda(String tipo, String equipo, int año, String talla, int cantidadStock, double precio, Blob imagen) {
		this.tipo = tipo;
		this.equipo = equipo;
		this.cantidadStock=cantidadStock;
		this.talla = talla;
		this.precio=precio;
		this.año = año;
		this.imagen = imagen;
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

	public void setTalla(String talla){
		this.talla = talla;
	}

	public int getCantidadVendida() {
		return cantidadVendida;
	}

	public void setCantidadVendida(int cantidadVendida) {
		this.cantidadVendida = cantidadVendida;
	}

	public int getCantidadStock() {
		return cantidadStock;
	}

	public void setCantidadStock(int cantidadStock) {
		this.cantidadStock = cantidadStock;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

}
