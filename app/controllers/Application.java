package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        renderText("Bienvenidos a nuestro proyecto");
    }
    
     //Registro cliente comprobando que no haya ninguno con "usuario" igual, los demas campos no son restrictivos//
   public static void registrarCliente( String nombre, String apellido1, String apellido2, String direccion, String usuario, String contraseña, int cuentabancaria) {
	   Cliente c= Cliente.find("byUsuario",usuario).first();
	   if(c==null) {
		   c= new Cliente(usuario,contraseña);
		   renderText("Cliente registrado correctamente en nuestra BD");
		   c.save();
	   }
	   else 
		   renderText("El nombre de usuaro introducido ya esta en uso, pruebe nuevamente");
   }
   //Procedo a eliminar cliente pidiendo su "usuario" y su "contraseña"//
   public static void eliminarCliente(String usuario, String contraseña) {
	   Cliente c= Cliente.find("byUsuarioAndContraseña",usuario, contraseña).first();
	   if(c!= null) {
		   c.delete();
		   renderText("Cliente con usuario: "+usuario+" y contraseña "+contraseña+"  eliminado de nuestra BD");
	   }
	   else 
		   renderText("Este cliente no existe");  
   }

    public static void AñadirCarrito (String tipo, String equipo, String talla, int cantidad){
    	Prenda p = Prenda.find("ByTipoAndEquipoAndTalla", tipo, equipo, talla).first();
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
	public static void AddStock(String tipo, String equipo, String talla, int cantidadStock, double precio){

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

	//localhost:9000/application/Login?usuario=maria&contraseña=mariac
	public static void Login(String usuario, String contraseña){
		//Cliente c1 = new Cliente("maria","mariac");
		//c1.save();
    	if(usuario!=null && contraseña!=null)
		{
    		Cliente c = Cliente.find("byUsuarioAndContraseña",usuario,contraseña).first();
			renderText(c.getUsuario()+" bienvenido a tu tienda online");
		}
		else{
			renderText("Introduce usuario y contraseña otra vez");
		}

	}






}