
package controllers;

import com.mysql.fabric.xmlrpc.Client;
import org.eclipse.jdt.internal.core.nd.field.StructDef;
import play.*;
import play.data.validation.Validation;
import play.mvc.*;

import java.security.SecureRandom;
import java.security.Security;
import java.util.*;

import models.*;

import javax.validation.Valid;
import javax.xml.transform.Result;

@With(Security.class)
public class Application extends Controller {

	static int visionadmin = 0;

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

	public static void index() {

		if(connected() != null) {
			renderTemplate("Application/principal.html");
		}
		else {
			renderTemplate("Application/loginTemplate.html");
		}
    }
	
	public static void loginTemplate(){
			render();
    }
	
	public static void register() {
	        render();
	}

	public static void recuperacionContra() {
			render();
	}
	
	public class Security extends Secure.Security {
	    	 boolean authenticate(String username, String password) {
	         Cliente cliente = Cliente.find("byUsuario", username).first();
	         return cliente != null && cliente.equals(password);
	    }
	}

   public static void Login(@Valid Cliente cliente) {
		Cliente c = Cliente.find("byUsuarioAndContraseña", cliente.usuario, cliente.contraseña).first();
		 if(c != null) {
	           session.put("user",cliente.usuario);
	           renderArgs.put("client", c);
	           if (c.admin == 1){
	           	   visionadmin = 0;
				   renderArgs.put("visionadmin", visionadmin);
				   renderTemplate("Application/principalAdmin.html");
			   }

	           else
	           	renderTemplate("Application/principal.html");
	        }
		 else
	            renderTemplate("Application/loginTemplate.html");
	}
	
   public static void registrarCliente(@Valid Cliente nuevocliente, String usuario, String contraseña, String mail, String nombre, String apellido1) {

		validation.required(usuario);
	    validation.required(contraseña);
	    validation.required(mail);
	   validation.required(nombre);
	   validation.required(apellido1);
	    validation.equals(contraseña, nuevocliente.contraseña).message("Las contraseñas no coinciden");
	    if(validation.hasErrors()) {
		   render("@register");
	    }

	   Cliente c = Cliente.find("byUsuario", usuario).first();

	   if (c == null) {
	   	   nuevocliente.usuario = usuario;
	   	   nuevocliente.mail = mail;
	   	   nuevocliente.nombre = nombre;
	   	   nuevocliente.apellido1 = apellido1;
		   nuevocliente.create();
		   session.put("user", nuevocliente);
		   renderArgs.put("client", nuevocliente);
		   renderTemplate("Application/principal.html");
	   }

	   else{
		   validation.equals(usuario, "").message("El usuario ya está en uso");
		   render("@register");
	   }
   }

	public static void añadirusuario() {

		if (visionadmin != 1)
		visionadmin=1;

		else
			visionadmin=0;

		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}

   public static void registrarclienteadmin(@Valid Cliente nuevocliente, String usuario, String nombre, String apellido1, String contraseña, String mail){

		validation.required(usuario);
		validation.required(nombre);
		validation.required(apellido1);
	   validation.required(contraseña);
	   validation.required(mail);
	   validation.equals(contraseña, nuevocliente.contraseña).message("Las contraseñas no coinciden");
	   if(validation.hasErrors()) {
		   renderArgs.put("visionadmin", visionadmin);
		   render("Application/principalAdmin.html");
	   }
	   Cliente c = Cliente.find("byUsuario", usuario).first();

	   if (c == null) {
	   	nuevocliente.nombre = nombre;
	   	nuevocliente.apellido1 = apellido1;
		   nuevocliente.usuario = usuario;
		   nuevocliente.mail = mail;
		   nuevocliente.create();
		   renderArgs.put("visionadmin", visionadmin);
		   validation.equals(usuario, "").message("Usuario registrado correctamente");
		   render("Application/principalAdmin.html");
	   }

	   else{
		   renderArgs.put("visionadmin", visionadmin);
		   validation.equals(usuario, "").message("El usuario ya está en uso");
		   renderTemplate("Application/principalAdmin.html");
	   }
   }

	public static void modificarusuarioadmin(){

		if (visionadmin != 2){
			List<Cliente> lclientes = Cliente.findAll();
			renderArgs.put("listaclientes", lclientes);
			visionadmin = 2;
		}

		else{
			visionadmin = 0;
		}

		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");

	}

	public static void cargardatos(Long idusuario){
		visionadmin = 3;
		List<Cliente> lclientes = Cliente.findAll();
		renderArgs.put("listaclientes", lclientes);
		Cliente modificar = Cliente.findById(idusuario);
		renderArgs.put("usuarioM",modificar);
		renderArgs.put("visionadmin", visionadmin);
		renderTemplate ("Application/principalAdmin.html");
	}

	public static void ModificarU(Cliente usuarioM){
		Cliente user = Cliente.find("byUsuario", usuarioM.usuario).first();
		user.usuario = usuarioM.usuario;
		user.contraseña = usuarioM.contraseña;
		user.mail =usuarioM.mail;
		user.nombre = usuarioM.nombre;
		user.apellido1 = usuarioM.apellido1;
		user.apellido2 = usuarioM.apellido2;
		user.save();
		List<Cliente> lclientes = Cliente.findAll();
		renderArgs.put("listaclientes", lclientes);
		visionadmin = 2;
		renderArgs.put("visionadmin", visionadmin);
		renderTemplate ("Application/principalAdmin.html");
	}

	public static void cargareliminar(){

		if (visionadmin != 4){
			List<Cliente> lclientes = Cliente.findAll();
			renderArgs.put("listaclientes", lclientes);
			visionadmin = 4;
		}

		else{
			visionadmin = 0;
		}

		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}

	public static void eliminarusuarioadmin (Long idusuario){

		visionadmin = 4;
		Cliente eliminar = Cliente.findById(idusuario);
		eliminar.delete();
		List<Cliente> lclientes = Cliente.findAll();
		renderArgs.put("listaclientes", lclientes);
		renderArgs.put("visionadmin", visionadmin);
		renderTemplate ("Application/principalAdmin.html");
	}

	public static void recuperarContra(@Valid Cliente cliente, String mail) {
	   validation.required(mail);
	   validation.equals(mail, cliente.mail).message("Los emails no coinciden");
	   if(validation.hasErrors()) {
		   render("@recuperacionContra",cliente, mail);
	   }
	   else {

		   Cliente c= Cliente.find("byMail", cliente.mail).first();
		   if(c!=null) {
			   renderText("La contraeña es:" +c.contraseña);
		   }
	   }
 
   }

   public static void goAddStock(){
		if (visionadmin != 5)
			visionadmin = 5;

		else
			visionadmin=0;

		renderArgs.put("visionadmin",visionadmin);
		renderTemplate("Application/principalAdmin.html");
	}

   public static void ModificarDatosAdmin(Cliente user, String username){

	   Cliente client = Cliente.find("byUsuario", username).first();
	   if (client != null) {
		   client.usuario = user.usuario;
		   client.contraseña = user.contraseña;
		   client.mail = user.mail;
		   client.save();
		   List<Cliente> lclientes = Cliente.findAll();
		   renderArgs.put("listaclientes", lclientes);
		   renderTemplate ("Application/principalAdmin.html");
	   }
   }

   public static void cargardatosusuario(Cliente c, String contraseña){
	   validation.required(contraseña);
	   String username = session.get("user");
	   Cliente usuario=Cliente.find("byUsuario", username).first();
	   validation.equals(contraseña, c.contraseña).message("La contraseña es incorrecta, no puede modificar datos");
	   if(validation.hasErrors()) {
		   render("@ModificarUsuario");
	   }
	   else{
	   	renderArgs.put("clienteM", usuario);
		   renderTemplate("Application/ModificarUsuario2.html");
	   }


   }

   public static void ModificarUsuario() {
	   render();
   }

   public static void ModificarDatos2(Cliente clienteM) {
	   String username = session.get("user");
	   Cliente c=Cliente.find("byUsuario", username).first();
	   if(c!=null) {
		   c.usuario=clienteM.usuario;
		   c.contraseña=clienteM.contraseña;
		   c.mail=clienteM.mail;
		   c._save();
		   renderTemplate("Application/principal.html");
	   }
   }

   public static void EliminarUsuario () {
	   render();
   }

   public static void eliminarusuariocontraseña (String contraseña){
		   validation.required(contraseña);
		   String username = session.get("user");
		   Cliente c=Cliente.find("byUsuario", username).first();
		   validation.equals(contraseña, c.contraseña).message("La contraseña es incorrecta, no puede eliminar su cuenta");
		   if(validation.hasErrors()) {
			   render("@EliminarUsuario");
		   }
		   else {
			   c.delete();
			   session.clear();
			   renderTemplate("Application/loginTemplate.html");
		   }
   }

   public static void quitarstock(){

		if (visionadmin != 6)
			visionadmin = 6;

		else
			visionadmin = 0;

		List<Prenda> lprendas = Prenda.findAll();
		List<String> lequipos = new ArrayList<String>();
	    lequipos.add(lprendas.get(0).equipo);
		boolean encontrado = false;
		int j;
		for(int i = 0; i<lprendas.size();i++){

			j = 0;

			while (j<lequipos.size() && !encontrado){
				if (lprendas.get(i).equipo == lequipos.get(j))
					encontrado = true;
				j++;
			}

			if (!encontrado)
				lequipos.add(lprendas.get(i).equipo);

			else
				encontrado = false;

		}

		renderArgs.put("listaequipos", lequipos);
		renderArgs.put("visionadmin", visionadmin);
	   renderTemplate("Application/principalAdmin.html");
   }

   public static void cargarequipo(Long idequipo){
		renderArgs.put("visionadmin", visionadmin);
	   renderTemplate("Application/principalAdmin.html");
   }

   public static void CambiarVistaNormal(){
	   renderTemplate("Application/principal.html");
   }

   public static void CambiarVistaAdmin(){
		visionadmin = 0;
	   renderArgs.put("visionadmin", visionadmin);
	   renderTemplate("Application/principalAdmin.html");
   }

   public static void registrarAndroid(String user, String password) {
	   Cliente c = Cliente.find("byUsuario",user).first();
	   if(c==null) {
		   c= new Cliente(user,password);
		   c._save();	   
		   renderText("OK, Cliente se puede registrar, añadido a la BD");

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

   public static void Logout (){
	   session.clear();
       renderArgs.put("client",null);
       renderTemplate("Application/loginTemplate.html");
   }

   public void AddStock(Prenda prendaM){

	   prendaM.equipo=prendaM.equipo.toUpperCase();
	   prendaM.tipo=prendaM.tipo.toUpperCase();
	   prendaM.talla = prendaM.talla.toUpperCase();
	   Prenda p = Prenda.find("byTipoAndEquipoAndTallaAndPrecio",prendaM.tipo,prendaM.equipo,prendaM.talla,prendaM.precio).first();

	   //Blob blob = prendaM.imagen;
	   //byte [] array = blob.getBytes(1l,(int)blob.length());
	   //File file = File.createTempFile();


	   if(p==null){
		   prendaM.cantidadStock=prendaM.cantidadComprada;
		   prendaM.create();
		   renderArgs.put("visionadmin", visionadmin);
		   renderTemplate("Application/principalAdmin.html");
	   }

	   else {
		   p.cantidadStock=prendaM.cantidadComprada+p.getCantidadStock();
		   p.setCantidadStock(p.cantidadStock);
		   p.save();
		   renderArgs.put("visionadmin", visionadmin);
		   renderTemplate("Application/principalAdmin.html");
	   }

   }
   public static void CargarPrendasEnPrincipal(Prenda prendaM){

		List<Prenda> prendas = Prenda.all().fetch(100);
		if(prendas != null){
			for(final Prenda p : prendas){
				render(prendas, prendaM);
			}
	   }
		else{
			renderText("no hay objetos de esta lista");
		}
   }

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