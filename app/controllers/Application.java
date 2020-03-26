package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

	public List<Prenda> carro;

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

}