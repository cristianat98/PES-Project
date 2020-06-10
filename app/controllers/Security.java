package controllers;

import javax.mail.Session;

import models.Cliente;

public class Security extends Secure.Security {
	    static boolean authenticate(String username, String password) {
	         Cliente c = Cliente.find("byUsuario", username).first();
	         return c != null && c.contraseña.equals(password);
	    }
	        
	    static boolean check(String profile) {
	        Cliente c = Cliente.find("byUsuario", connected()).first();
	        if("admin".equals(profile)) {
	        	return c.adminsecure;
	        }
	        else {
	        	return false;
	        }
	        
	    }
}
	    	


