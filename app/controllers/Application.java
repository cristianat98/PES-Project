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
				renderText("Cliente registrado correctamente en nuestra BD. Pruebe ahora a loguearse con esta misma cuenta.");
				c.save();
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

		Cliente c = Cliente.find("byLogueado", true).first();

		if (c != null)
			renderText("Ya estás logueado como " + c.getUsuario());

		else {

			if (usuario != null && contraseña != null) {

				c = Cliente.find("byUsuarioAndContraseña", usuario, contraseña).first();

				if (c == null)
					renderText("Los datos introducidos no son correctos");

				else{
					renderText("Te has logueado como " + c.getUsuario());
					c.setLogueado(true);
				}
			}

		}
	}

	//Procedo a eliminar cliente solo si ha iniciado sesión
	//localhost:9000/application/eliminarCliente
   public void eliminarCliente(String usuario, String contraseña) {

	   if(log!= null) {
		   log.delete();
		   renderText("Cliente con usuario: " + log.getUsuario() + " y contraseña "+ log.getContraseña() + "  eliminado de nuestra BD");
	   }
	   else 
		   renderText("Debes loguearte con una cuenta.");
   }

    public static void AñadirCarrito (String tipo, String equipo, String talla, int cantidad){
    	Prenda p = Prenda.find("byTipoAndEquipoAndTalla", tipo, equipo, talla).first();
    	if (p != null)
		{
			if (p.getCantidadStock() >= cantidad)
				renderText("Vestimenta añadida al carrito");
			
			else
				renderText("Solo podemos añadir al carro "+ p.getCantidadStock());
		}
    	else
    		renderText("No tenemos esa vestimenta disponible actualmente.");
   }
	//localhost:9000/application/AddStock?tipo=camiseta&equipo=Albacete&talla=4&cantidadStock=9&precio=78.9
	public void AddStock(String tipo, String equipo, String talla, int cantidadStock, double precio){

		if(tipo==null || equipo==null || talla==null || cantidadStock <=0 || precio<= 0)
		{
			renderText("Imposible añadir, parametros no adecuados");
		}
		else
		{
			Prenda p = Prenda.find("byTipoAndEquipo",tipo,equipo).first();
			if(p==null){

				p = new Prenda(tipo, equipo,talla,cantidadStock,precio);
				p.save();
				renderText(p.getCantidadStock());
			}
			else {
				cantidadStock=cantidadStock+p.getCantidadStock();
				p.setCantidadStock(cantidadStock);
				renderText(cantidadStock);
			}
		}
	}




}