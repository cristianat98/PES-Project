package controllers;

import org.eclipse.jdt.internal.core.nd.field.StructDef;
import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

import javax.validation.Valid;

public class Application extends Controller {

	@Before
	static void addUser() {
		Cliente user = connected();

		if(user != null) {
			renderArgs.put("client", user);
		}
	}

	static Cliente connected() {
		if(renderArgs.get("client") != null) {
			return renderArgs.get("client", Cliente.class);
		}
		String username = session.get("user");
		if(username != null) {
			return Cliente.find("byUsuario", username).first();
		}
		return null;
	}

	//public static List<Prenda> carro;

	//Función que se ejecuta con el localhost:9000
	
	public static void index() {

		if(connected() != null) {
			renderText("Ahora mismo esta conectado " +session.get("user"));
			//renderTemplate("Application/Principal.html");
		}
		else {
			renderTemplate("Application/loginTemplate.html");
		}
    }
	
	
	public static void getInfoSession(){
        renderText("Esta  conectado "+ session.get("user"));
    }

	public static void register() {
	        render();
	}

 
   public static void registrarCliente(@Valid Cliente nuevocliente, String contraseña) {

	   validation.required(contraseña);
	   validation.equals(contraseña, nuevocliente.contraseña).message("Las contraseñas no coinciden");
	   if(validation.hasErrors()) {
		   render("@register", nuevocliente, contraseña);
	   }


	   if (Cliente.find("byUsuarioAndContraseña", nuevocliente.usuario, nuevocliente.contraseña).first() == null) {
			   nuevocliente.create();
			   session.put("user", nuevocliente.usuario);
			   renderArgs.put("client", nuevocliente);
			   renderTemplate("Application/loginTemplate.html");
			   renderText("Usuari registrat " + nuevocliente.usuario);
	   }
	   else
	   	renderText(nuevocliente.usuario + " " + nuevocliente.contraseña);
   }
   
   
   public static void registrarAndroid(String user, String password) {
	   Cliente c = Cliente.find("byUsuario",user).first();
	   if(c==null) {
		   c= new Cliente(user,password);
		   c._save();
		   renderText("Cliente añadido a la BD");
	   }
	   else
		   renderText("FAIL, este nombre de usuario ya existe");
   }
   
	public static void loginAndroid(String user, String password){
		Cliente c = Cliente.find("byUsuarioAndContraseña", user, password).first();
		if(c!=null) {
			renderText("OK ,este cliente esta en la BD");
		}
		else 
			renderText("FAIL este cliente no esta en la BD ");	
	}

	
	public static void Login(@Valid Cliente cliente) {
		Cliente c = Cliente.find("byUsuarioAndContraseña", cliente.usuario, cliente.contraseña).first();
		 if(c != null) {
	           session.put("user",cliente.usuario);
	           renderArgs.put("client", c);
	           renderTemplate("Application/principal.html");
	        }
		 else {
	            renderTemplate("Application/loginTemplate.html");
	        }
	}
	
	//Procedo a eliminar cliente con sus datos
	//localhost:9000/application/eliminarCliente?usuario=cristian
	//localhost:9000/application/eliminarCliente?contraseña=1234
	//localhost:9000/application/eliminarCliente?usuario=cristian&contraseña=1234
	//localhost:9000/application/eliminarCliente?usuario=cristian&contraseña=1234
   public void eliminarCliente(String usuario, String contraseña) {

		if (usuario == null && contraseña == null)
			renderText("No has introducido todos los datos.");
		else{
			Cliente c = Cliente.find("byUsuarioAndContraseña", usuario, contraseña).first();
			if(c!= null) {
				c.delete();
				renderText("Cliente con usuario: " + c.getUsuario() + " y contraseña "+ c.getContraseña() + "  eliminado de nuestra BD");
			}
			else
				renderText("Los datos introducidos no son correctos.");
		}
   }

   public static void Logout (){
		session.clear();
		renderTemplate("Application/loginTemplate.html");
   }
	//Función que añade Stocka la tienda
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
	//localhost:9000/application/comprar?tipo=camiseta&equipo=Albacete&talla=L&cantidad=1&usuario=cristian&contraseña=123
	//localhost:9000/application/comprar?tipo=camiseta&equipo=Albacete&talla=L&cantidad=1&usuario=cristian&contraseña=1234
	//localhost:9000/application/comprar?tipo=camiseta&equipo=Albacete&talla=M&cantidad=1&usuario=cristian&contraseña=1234
	//localhost:9000/application/comprar?tipo=camiseta&equipo=Albacete&talla=M&cantidad=50&usuario=cristian&contraseña=1234
	public static void comprar (String tipo, String equipo, String talla, int cantidad, String usuario, String contraseña){

		if (tipo == null || equipo == null || talla == null || cantidad < 1 || usuario == null || contraseña == null)
				renderText("No has introducido todos los datos.");

			else {
			Cliente c = Cliente.find("byUsuarioAndContraseña", usuario, contraseña).first();

			if (c == null)
				renderText("Ese usuario no existe");
			else
				{
				Prenda p = Prenda.find("byTipoAndEquipoAndTalla", tipo, equipo, talla).first();
				if (p != null) {
					if (p.getCantidadStock() >= cantidad) {
						Date fecha = new Date();
						p.setCantidadStock(p.getCantidadStock() - cantidad);
						p.setCantidadComprada(p.getCantidadComprada() + cantidad);
						Compra Compra = new Compra(c, p, fecha);
						Compra.save();
						renderText("Has comprado " + cantidad + " " + tipo + " del " + equipo);
					} else
						renderText("No tenemos tanto stock en la tienda. Vuelve a realizar el pedido con el número disponible.");
				} else
					renderText("No tenemos esa vestimenta disponible actualmente.");
			}

		}

	}
}