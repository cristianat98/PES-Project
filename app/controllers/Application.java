package controllers;

import org.eclipse.jdt.internal.core.nd.field.StructDef;
import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

	public List<Prenda> carro;

	//Mensaje que aparece en localhost:9000
	public static void index() {
        renderText("Bienvenidos a nuestro proyecto de PES");
    }
    
    //Registro cliente comprobando que no haya ninguno con "usuario" igual, los demas campos no son restrictivos
	//localhost:9000/application/registrarCliente?contraseña=1234
	//localhost:9000/application/registrarCliente?usuario=cristian
	//localhost:9000/application/registrarCliente?usuario=cristian&contraseña=1234
	//localhost:9000/application/registrarCliente?usuario=david&contraseña=4321
	//localhost:9000/application/registrarCliente?usuario=fernand&contraseña=1111
	//localhost:9000/application/registrarCliente?usuario=cristian&contraseña=12345
   public void registrarCliente(String usuario, String contraseña) {

    	if (usuario == null || contraseña == null)
    		renderText ("No has introducido todos los datos.");

    	else{
			Cliente c= Cliente.find("byUsuario",usuario).first();

			if(c==null) {

				c= new Cliente(usuario,contraseña);
				c.save();
				renderText("Cliente registrado correctamente en nuestra BD. Pruebe ahora a loguearse con esta misma cuenta.");
			}
			else
				renderText("El nombre de usuaro introducido ya esta en uso, pruebe nuevamente con otro.");
		}

   }

	//Iniciamos sesión comprobando que los datos introducidos son los correctos
    //localhost:9000/application/Login?usuario=cristian
	//localhost:9000/application/Login?contraseña=1234
	//localhost:9000/application/Login?usuario=cris&contraseña=1234
	//localhost:9000/application/Login?usuario=cristian&contraseña=1234
	//localhost:9000/application/Login?usuario=david&contraseña=4321
	public void Login(String usuario, String contraseña) {

		Cliente c = Cliente.find("byLogueado", 1).first();

		if (c != null)
			renderText("Ya estás logueado como " + c.getUsuario());

		else {

			if (usuario != null && contraseña != null) {

				c = Cliente.find("byUsuarioAndContraseña", usuario, contraseña).first();

				if (c == null)
					renderText("Los datos introducidos no son correctos");

				else{
					c.setLogueado(1);
					renderText("Te has logueado como " + c.getUsuario());
				}
			}

		}
	}

	//Cerramos sesión en caso de estar logueado
	//localhost:9000/application/cerrarSesion
	public void cerrarSesion(){

		Cliente c = Cliente.find("byLogueado", 1).first();
		if (c == null)
			renderText("No estás logueado con ninguna cuenta.");

		else {
			c.setLogueado(0);
			renderText("Has cerrado sesión");
		}
	}

	//Procedo a eliminar cliente solo si ha iniciado sesión
	//localhost:9000/application/eliminarCliente
   public void eliminarCliente() {

		Cliente c = Cliente.find("byLogueado", 1).first();
	   if(c!= null) {
		   c.delete();
		   renderText("Cliente con usuario: " + c.getUsuario() + " y contraseña "+ c.getContraseña() + "  eliminado de nuestra BD");
	   }
	   else 
		   renderText("Debes loguearte con una cuenta.");
   }

	//Función que añade Stock a la tienda
    //localhost:9000/application/AddStock?tipo=camiseta&equipo=Albacete&talla=M&cantidadStock=0&precio=78.90
    //localhost:9000/application/AddStock?tipo=camiseta&equipo=Albacete&talla=M&cantidadStock=10&precio=78.90
	//localhost:9000/application/AddStock?tipo=camiseta&equipo=Albacete&talla=M&cantidadStock=10&precio=78.90
	public void AddStock(String tipo, String equipo, String talla, int cantidadStock, double precio){

		if(tipo==null || equipo==null || talla==null || cantidadStock <=0 || precio<= 0)
		{
			renderText("Imposible añadir, parametros no adecuados");
		}
		else
		{
			Prenda p = Prenda.find("byTipoAndEquipoAndTalla",tipo,equipo,talla).first();

			if(p==null){
				p = new Prenda(tipo, equipo,talla,cantidadStock,precio);
				p.save();
				renderText("Se han añadido " + p.getCantidadStock() + " " + p.getTipo() + " del " + p.getEquipo());
			}

			else {
				cantidadStock=cantidadStock+p.getCantidadStock();
				p.setCantidadStock(cantidadStock);
				renderText("Actualmente tenemos " + cantidadStock + " " + p.getTipo() + " del " + p.getEquipo());
			}
		}
	}

	//Función que permite comprar ropa de la tienda
	//localhost:9000/application/comprar?tipo=camiseta
	//localhost:9000/application/comprar?tipo=camiseta&equipo=Albacete&talla=L&cantidad=1
	//localhost:9000/application/comprar?tipo=camiseta&equipo=Albacete&talla=M&cantidad=1
	//localhost:9000/application/comprar?tipo=camiseta&equipo=Albacete&talla=M&cantidad=50
	public static void comprar (String tipo, String equipo, String talla, int cantidad){

		Cliente c = Cliente.find("byLogueado", 1).first();
		if (c==null)
			renderText("Debes loguearte con una cuenta");

		else{
			if (tipo == null || equipo == null || talla == null || cantidad < 1)
				renderText("No has introducido todos los datos.");

			else {
				Prenda p = Prenda.find("byTipoAndEquipoAndTalla", tipo, equipo, talla).first();
				if (p != null) {
					if (p.getCantidadStock() >= cantidad) {
						Date fecha = new Date();
						p.setCantidadStock(p.getCantidadStock() - cantidad);
						p.setCantidadComprada(p.getCantidadComprada()+cantidad);
						Compra Compra = new Compra (c, p, fecha);
						Compra.save();
						renderText("Has comprado " + cantidad + " " + tipo + " del " + equipo);
					}

					else
						renderText("No tenemos tanto stock en la tienda. Vuelve a realizar el pedido con el número disponible.");
				} else
					renderText("No tenemos esa vestimenta disponible actualmente.");
			}
		}

	}



}