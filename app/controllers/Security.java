package controllers;

import javax.mail.Session;


import models.Cliente;

public class Security extends Secure.Security {

	    static boolean authenticate(String username, String password) {
	         Cliente c = Cliente.find("byUsuario", username).first();
	         if(c!=null && c.getContraseña().equals(password)) {
	        	 return true;
	         }else {
	        	 return false;
	         }
	         
	    }
//	    static boolean check(String profile) {
	//		    	Cliente cc= connected();
	//		    	Cliente c= Cliente.find("byUsuario",cc.usuario).first();
	//	 	if("c.usuario".equals(Profile)) {
	    	//	    		return true;
	    	//	    	}
	    //	    	else{
	    	//	  		return false;
	    	//	  	}

	    	//	  }
	    
}