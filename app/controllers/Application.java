package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	
        renderText("Buenas tardes, esto es un una prueba de mi proyecto");
    }
    public static void inicializarBD() {
    	Cliente c1= new Cliente("David", "chiki","1234").save();
    	Cliente c2=new Cliente ("Cristian","cristian","4321").save();
    	Prenda p1= new Prenda("Camiseta","Madrid","M");
    	Prenda p2= new Prenda("Pantalon","Barcelona","XL");
    	
    	p1.cliente=c1;
    	p1.save();
    	p2.cliente=c1;
    	p2.save();

    	renderText("Buenas tardes, la base de datos ha sido creada");

    }
    public static void añadirCliente(String nombre,String nombreusuario, String password) {
    	Cliente c= Cliente.find("byNombreusuario",nombreusuario).first();
    	if(c==null) {
    		if (password == null){
    			renderText("Te falta una password");
			}

    		else {
				new Cliente(nombre, nombreusuario, password).save();
				renderText("Cliente añadido a la BD");
			}
    	}

    	else {
    		renderText("Este cliente ya se encuentra en la BD");
    	}
    	
    }
}