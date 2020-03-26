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
	@ManyToMany
	public List <Prenda> prenda;
	public Date fecha;
	
	
	public Compra(Cliente cliente, List <Prenda> prenda,Date fecha) {
		super();
		this.cliente = cliente;
		this.prenda= prenda;
		this.fecha = fecha;
	}
	

	

}
