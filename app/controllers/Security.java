package controllers;

import models.Cliente;

public class Security extends Secure.Security {
    
    static boolean authenticate(String username, String password) {
    	Cliente c = Cliente.find("byUsername", username).first();
        if(c!=null && c.getContraseña().equals(password)) {
        	return true;
        }else
        	return false;
    	}
}
