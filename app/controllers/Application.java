
package controllers;

import com.sun.org.apache.xpath.internal.operations.Bool;
import jdk.nashorn.internal.runtime.regexp.joni.ast.CClassNode;
import org.hibernate.annotations.Check;
import play.data.validation.Validation;
import play.db.jpa.Blob;
import play.db.jpa.JPABase;
import play.mvc.*;

import java.io.File;
import java.io.InputStream;
import java.util.*;

import models.*;

import javax.validation.Valid;


@With(Secure.class)
public class Application extends Controller {

	static int visionadmin = 0;
	static Prenda referencia;
	static Cliente visualizar;
	static List<Prenda> carrito = new ArrayList<>();
	static String equipo = "";
	static String tipo = "";
	static String talla = "";
	static int año = 0;


	//FUNCIONES LARGAS
	static List<Prenda> OrdenarPrendas(int a) {

		List<Prenda> prendas = Prenda.findAll();

		for (int i = 0; i < prendas.size(); i++) {
			for (int j = i + 1; j < prendas.size(); j++) {
				if (prendas.get(i).tipo.equals(prendas.get(j).tipo) && prendas.get(i).equipo.equals(prendas.get(j).equipo) && prendas.get(i).año == prendas.get(j).año) {
					prendas.remove(j);
					j--;
				}
			}
		}

		List<Integer> años = new ArrayList<Integer>();
		List<String> equipos = new ArrayList<String>();

		for (int i = 0; i < prendas.size(); i++) {
			equipos.add(prendas.get(i).equipo);
			años.add(prendas.get(i).año);
		}

		List<Prenda> listaprendas = new ArrayList<Prenda>();
		Collections.sort(años);

		for (int i = 0; i < años.size(); i++) {
			for (int j = 0; j < prendas.size(); j++) {
				if (años.get(i) == prendas.get(j).año) {
					listaprendas.add(prendas.get(j));
					prendas.remove(j);
					j--;
				}
			}
		}

		prendas = listaprendas;
		listaprendas = new ArrayList<>();
		Collections.sort(equipos);

		for (int i = 0; i < equipos.size(); i++) {
			for (int j = 0; j < prendas.size(); j++) {
				if (equipos.get(i).equals(prendas.get(j).equipo)) {
					listaprendas.add(prendas.get(j));
					prendas.remove(j);
					j--;
				}
			}
		}

		if (a == 1){
			List<Prenda> listaprenda = new ArrayList<>();

			for (int i = 0; i < listaprendas.size(); i++) {
				Prenda prenda = listaprendas.get(i);
				List<Prenda> prendax = Prenda.find("byTipoAndEquipoAndAño", prenda.tipo, prenda.equipo, prenda.año).fetch();
				for (int j = 0; j < prendax.size(); j++) {
					if (prendax.get(j).talla != null)
						listaprenda.add(prendax.get(j));
				}
			}
			listaprendas = listaprenda;
		}

		return listaprendas;
	}

	static List<Prenda> FiltrarPrendas(List<Prenda> prendas){

		if (!equipo.equals("")){
			for (int i = 0;i<prendas.size(); i++){
				if (!equipo.equals(prendas.get(i).equipo)){
					prendas.remove(i);
					i--;
				}
			}
			equipo = "";
		}

		if (!talla.equals("")){
			for (int i = 0;i<prendas.size(); i++){
				if (!talla.equals(prendas.get(i).talla)){
					prendas.remove(i);
					i--;
				}
			}
			talla = "";
		}

		if (año != 0){
			for (int i = 0;i<prendas.size(); i++){
				if (año != prendas.get(i).año){
					prendas.remove(i);
					i--;
				}
			}
			año = 0;
		}
		return prendas;
	}

	@Before
	static void addUser() {
		Cliente user = connected();

		if (user != null) {
			renderArgs.put("client", user);
		}
	}

	static Cliente connected() {
		if (renderArgs.get("client") != null) {
			return renderArgs.get("client", Cliente.class);
		}

		String username = session.get("user");
		if (username != null) {
			return Cliente.find("byUsuario", username).first();
		}

		return null;
	}

	//INDEX
	public static void index() {

		if (connected() != null) {
			List<Prenda> todas = OrdenarPrendas(0);
			List<Prenda> camisetas = new ArrayList<>();
			for (int i = 0; i < todas.size(); i++) {
				if (todas.get(i).cantidadStock != 0 && todas.get(i).tipo.equals("CAMISETA"))
					camisetas.add(todas.get(i));
			}

			List<String> camisetaequipos = new ArrayList<>();
			List<String> camisetatallas = new ArrayList<>();
			List<Integer> camisetaaños = new ArrayList<>();

			for (int i = 0; i < camisetas.size(); i++) {
				if (!camisetaequipos.contains(camisetas.get(i).equipo))
					camisetaequipos.add(camisetas.get(i).equipo);
				if (!camisetatallas.contains(camisetas.get(i).talla))
					camisetatallas.add(camisetas.get(i).talla);
				if (!camisetaaños.contains(camisetas.get(i).año))
					camisetaaños.add(camisetas.get(i).año);
			}

			List<Prenda> pantalones = new ArrayList<>();
			for (int i = 0; i < todas.size(); i++) {
				if (todas.get(i).cantidadStock != 0 && todas.get(i).tipo.equals("PANTALON"))
					pantalones.add(todas.get(i));
			}

			List<String> pantalonequipos = new ArrayList<>();
			List<String> pantalontallas = new ArrayList<>();
			List<Integer> pantalonaños = new ArrayList<>();

			for (int i = 0; i < pantalones.size(); i++) {
				if (!pantalonequipos.contains(pantalones.get(i).equipo))
					pantalonequipos.add(pantalones.get(i).equipo);
				if (!pantalontallas.contains(pantalones.get(i).talla))
					pantalontallas.add(pantalones.get(i).talla);
				if (!pantalonaños.contains(pantalones.get(i).año))
					pantalonaños.add(pantalones.get(i).año);
			}

			if (tipo.equals("Camiseta")) {
				tipo = "";
				camisetas = FiltrarPrendas(camisetas);
			} else if (tipo.equals("Pantalon")) {
				tipo = "";
				pantalones = FiltrarPrendas(pantalones);
			}

			renderArgs.put("camisetas", camisetas);
			renderArgs.put("pantalones", pantalones);
			renderArgs.put("carrito", carrito);
			renderArgs.put("camisetaequipos", camisetaequipos);
			renderArgs.put("camisetatallas", camisetatallas);
			renderArgs.put("camisetaaños", camisetaaños);
			renderArgs.put("pantalonequipos", pantalonequipos);
			renderArgs.put("pantalontallas", pantalontallas);
			renderArgs.put("pantalonaños", pantalonaños);
			render("Application/principal.html");
		} else
			render("Application/loginTemplate.html");

	}


	//ACCIONES PRIMERA VEZ

	//ANDROID
	public static void registrarAndroid(String user, String password) {
		Cliente c = Cliente.find("byUsuario", user).first();
		if (c == null) {
			String mail = user + "@es";
			c = new Cliente(user, user, user, password, mail);
			c._save();
			renderText("OK, Cliente se puede registrar, añadido a la BD");
		} else
			renderText("FAIL, este nombre de usuario ya existe");
	}

	public static void loginAndroid(String user, String password) {
		Cliente c = Cliente.find("byUsuarioAndContraseña", user, password).first();
		if (c != null) {
			renderText("OK ,este cliente esta en la BD");
		} else
			renderText("FAIL este cliente no esta en la BD ");
	}


	//SERVIDOR WEB
	public static void Login(@Valid Cliente cliente, String adm) {

		Cliente c = Cliente.find("byUsuarioAndContraseña", cliente.usuario, cliente.contraseña).first();
		if (c != null) {
			if (adm.equals("Entrar Como Administrador")) {
				if (c.admin == 0) {
					String erroradmin = "erroradmin";
					renderArgs.put("erroradmin", erroradmin);
					render("Application/loginTemplate.html");
				} else {
					session.put("user", cliente.usuario);
					renderArgs.put("client", c);
					renderArgs.put("visionadmin", visionadmin);
					renderArgs.put("client", c);
					renderTemplate("Application/principalAdmin.html");
				}
			} else {
				session.put("user", cliente.usuario);
				renderArgs.put("client", c);
				index();
			}

		} else {
			String error = "error";
			renderArgs.put("error", error);
			render("Application/loginTemplate.html");
		}
	}

	public static void loginTemplate() {
		render();
	}

	public static void register() {
		render();
	}

	public static void registrarCliente(@Valid Cliente nuevocliente, String usuario, String contraseña, String mail, String nombre, String apellido1) {

		validation.required(usuario);
		validation.required(contraseña);
		validation.required(mail);
		validation.required(nombre);
		validation.required(apellido1);
		validation.equals(contraseña, nuevocliente.contraseña).message("Las contraseñas no coinciden");
		if (validation.hasErrors())
			render("@register");


		Cliente comprobarusuario = Cliente.find("byUsuario", usuario).first();
		Cliente comprobarcorreo = Cliente.find("byMail", mail).first();
		if (comprobarcorreo == null && comprobarusuario == null) {
			nuevocliente.usuario = usuario;
			nuevocliente.mail = mail;
			nuevocliente.nombre = nombre;
			nuevocliente.apellido1 = apellido1;
			nuevocliente.create();
			session.put("user", nuevocliente.usuario);
			index();
		}
		else if (comprobarusuario != null)
			validation.equals(usuario, "").message("El usuario ya está en uso");

		else
			validation.equals(mail, "").message("El correo ya está en uso");

		render("@register");
	}

	public static void recuperacionContra() {
		render();
	}

	public static void recuperarContra(@Valid Cliente cliente, String mail) {
		validation.required(mail);
		validation.equals(mail, cliente.mail).message("El email no es correcto");
		if (validation.hasErrors()) {
			render("@recuperacionContra", cliente, mail);
		} else {
			Cliente c = Cliente.find("byMail", cliente.mail).first();
			if (c != null){
				renderArgs.put("contraseña", c.contraseña);
				render("Application/loginTemplate.html");
			}
		}
	}


	//CARGAR PÁGINA
	public static void MostrarPrenda(Long id) {

		Prenda prenda = Prenda.findById(id);
		renderBinary(prenda.imagen.get());
	}

	public static void MostrarPerfil(Long id) {

		Cliente c = Cliente.findById(id);
		c.perfil.toString();
		if (c.perfil.get() == null)
			renderBinary(new File("C:/Users/cristian/Desktop/play-1.5.3/Proyecto-PES/public/images/avatar.jpg"));

		else
			renderBinary(c.perfil.get());
	}

	public static void Filtrar(String tipoform, String equipoform, String tallaform, int añoform){

		tipo = tipoform;
		equipo = equipoform;
		talla = tallaform;
		año = añoform;

		index();
	}


	//FUNCIONES CARRITO
	public static void AddCarrito(Long id) {

		Prenda prenda = Prenda.findById(id);
		Prenda p = new Prenda(prenda.tipo, prenda.equipo, prenda.año, prenda.talla, 1, prenda.precio, prenda.imagen);
		boolean encontrado = false;
		int i = 0;
		while (i < carrito.size() && !encontrado) {
			if (carrito.get(i).tipo.equals(p.tipo) && carrito.get(i).equipo.equals(p.equipo) && carrito.get(i).talla.equals(p.talla) && carrito.get(i).año == p.año) {
				carrito.get(i).cantidadStock++;
				encontrado = true;
			}
			i++;
		}

		if (!encontrado)
			carrito.add(p);

		index();
	}

	public static void MostrarPrendaCarrito (String tipo, String equipo, int año){

		Prenda p = Prenda.find("byTipoAndEquipoAndAño", tipo, equipo, año).first();
		renderBinary(p.imagen.get());
	}

	public static void comprar(){

		for(int i = 0;i<carrito.size();i++){
			Prenda p = Prenda.find("byTipoAndEquipoAndTallaAndAño", carrito.get(i).tipo, carrito.get(i).equipo, carrito.get(i).talla, carrito.get(i).año).first();
			Compra compra = new Compra(connected(), p, carrito.get(i).cantidadStock);
			compra.create();
			p.cantidadStock = p.cantidadStock - carrito.get(i).cantidadStock;
			p.cantidadVendida = p.cantidadVendida + carrito.get(i).cantidadStock;
			p.save();
		}

		carrito.clear();
		index();
	}


	//ACCIONES USUARIO

	//MODIFICAR PERFIL
	public static void ModificarUsuario() {
		renderArgs.put("mensaje", "Introduzca la contraseña para poder modificar sus datos:");
		renderArgs.put("modificar", 0);
		render();
	}

	public static void cargardatosusuario(String contraseña){
		int i = 0;
		validation.required(contraseña);
		String username = session.get("user");
		Cliente usuario=Cliente.find("byUsuario", username).first();
		validation.equals(contraseña, usuario.contraseña).message("La contraseña es incorrecta, no puede modificar datos");
		renderArgs.put("mensaje", "Introduzca la contraseña para poder modificar sus datos:");
		renderArgs.put("modificar", i);
		if(validation.hasErrors()) {
			render("@ModificarUsuario");
		}
		else{
			renderArgs.put("modificar", 1);
			renderArgs.put("mensaje", "Sus datos personales son:");
			renderArgs.put("clienteM", usuario);   //Variable en HTML , Variable en java//
			renderTemplate("Application/ModificarUsuario.html");
		}
	}

	public static void ModificarDatos2(Cliente clienteM, String usuario, String nombre, String apellido1, String mail, String contraseña, int perfil) {

		validation.required(usuario);
		validation.required(nombre);
		validation.required(apellido1);
		validation.required(mail);
		Cliente c = Cliente.find("byUsuario", session.get("user")).first();
		validation.equals(contraseña, clienteM.contraseña).message("Las contraseñas no coinciden");
		if(validation.hasErrors()) {
			int i = 1;
			renderArgs.put("clienteM", c);
			renderArgs.put("modificar", 1);
			render("@ModificarUsuario");
		}

		Cliente comprobarusuario = Cliente.find("byUsuario", usuario).first();
		Cliente comprobarcorreo = Cliente.find("byMail", mail).first();

		if (comprobarcorreo == c)
			comprobarcorreo = null;

		if (comprobarusuario == c)
			comprobarusuario = null;

		String error = null;
		if(comprobarcorreo == null && comprobarusuario == null) {

			c.usuario = usuario;
			session.put("user", c.usuario);
			c.mail = mail;
			c.nombre = nombre;
			c.apellido1 = apellido1;
			c.apellido2 = clienteM.apellido2;

			if (!clienteM.contraseña.equals(""))
				c.contraseña = clienteM.contraseña;

			if (clienteM.perfil != null)
				c.perfil = clienteM.perfil;

			if (perfil == 1)
				c.perfil = null;

			c.save();
			index();
		}

		else if (comprobarcorreo != null)
			error = "Este correo ya está en uso";

		else
			error = "Este nombre de usuario ya está en uso";

		renderArgs.put("modificar", 1);
		renderArgs.put("error", error);
		renderArgs.put("clienteM", c);
		render("Application/ModificarUsuario.html");
	}


	//BORRAR PERFIL
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
			if (c.admin == 1){
				List<Cliente> listaadmin = Cliente.find("byAdmin", 1).fetch();
				if (listaadmin.size() == 1){
					String error = "No puede eliminar esta cuenta, es la última con permisos de administrador";
					renderArgs.put("error", error);
					render("Application/EliminarUsuario.html");
				}

				else{
					List<Compra> compras = Compra.find("byCliente", connected()).fetch();
					for (int i = 0; i<compras.size(); i++){
						compras.get(i).delete();
						compras.remove(i);
						i--;
					}
					c.delete();
				}

			}

			else{
				List<Compra> compras = Compra.find("byCliente", connected()).fetch();
				for (int i = 0; i<compras.size(); i++){
					compras.get(i).delete();
					compras.remove(i);
					i--;
				}
				c.delete();
			}

			session.clear();
			render("Application/loginTemplate.html");
		}
	}


	//CERRAR SESIÓN
	public static void Logout (){
		session.clear();
		visionadmin = 0;
		//renderArgs.put("client",null);
		render("Application/loginTemplate.html");
	}



	//FUNCIONES ADMINISTRADOR
	public static void CambiarVistaNormal(){
		index();
	}

	public static void CambiarVistaAdmin(){
		  visionadmin = 0;
		  renderArgs.put("visionadmin", visionadmin);
		  renderTemplate("Application/principalAdmin.html");
	}


   //AÑADIR USUARIO
	public static void añadirusuario() {

		if (visionadmin != 1)
		visionadmin=1;

		else
			visionadmin=0;

		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}

   public static void registrarclienteadmin(@Valid Cliente nuevocliente, String usuario, String nombre, String apellido1, String contraseña, String mail, int admin){

	   validation.required(usuario);
	   validation.required(nombre);
	   validation.required(apellido1);
	   validation.required(contraseña);
	   validation.required(mail);
	   validation.equals(contraseña, nuevocliente.contraseña).message("Las contraseñas no coinciden");
	   if(validation.hasErrors()) {
		   String error = "No has introducido todos los datos";
		   renderArgs.put("error", error);
		   renderArgs.put("visionadmin", visionadmin);
		   render("Application/principalAdmin.html");
	   }

	   Cliente comprobarusuario = Cliente.find("byUsuario", usuario).first();
	   Cliente comprobarcorreo = Cliente.find("byMail", mail).first();
	   if (comprobarusuario == null && comprobarcorreo == null) {
	   		nuevocliente.nombre = nombre;
	   		nuevocliente.apellido1 = apellido1;
	   		nuevocliente.usuario = usuario;
	   		nuevocliente.mail = mail;
	   		nuevocliente.admin = admin;
	   		nuevocliente.create();
	   		String mensaje = "Usuario registrado correctamente";
	   		renderArgs.put("mensaje", mensaje);
	   		renderArgs.put("visionadmin", visionadmin);
	   		renderTemplate("Application/principalAdmin.html");
	   }

	   else if (comprobarcorreo == null){
		   renderArgs.put("visionadmin", visionadmin);
		   String error = "Este nombre de usuario ya está en uso";
		   renderArgs.put("error", error);
		   renderTemplate("Application/principalAdmin.html");
	   }

	   else{
		   renderArgs.put("visionadmin", visionadmin);
		   String error = "Este correo ya está en uso";
		   renderArgs.put("error", error);
		   renderTemplate("Application/principalAdmin.html");
	   }
   }


   //MODIFICAR USUARIO
	public static void modificarusuarioadmin(){

		if (visionadmin != 2){
			List<Cliente> lclientes = Cliente.findAll();
			renderArgs.put("listaclientes", lclientes);
			visionadmin = 2;
		}

		else
			visionadmin = 0;

		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");

	}

	public static void cargardatos(Long idusuario){
		List<Cliente> lclientes = Cliente.findAll();
		renderArgs.put("listaclientes", lclientes);
		Cliente modificar = Cliente.findById(idusuario);
		if (modificar.admin == 0)
			renderArgs.put("admin", 1);
		visualizar = modificar;
		renderArgs.put("usuarioM",modificar);
		renderArgs.put("visionadmin", visionadmin);
		renderTemplate ("Application/principalAdmin.html");
	}

	public static void ModificarU(Cliente usuarioM, String usuarioinicial, String usuariofinal, String nombre, String apellido1, String mail, int admin, int perfil){

		Cliente c = Cliente.find("byUsuario", usuarioinicial).first();
		validation.required(usuariofinal);
		validation.required(nombre);
		validation.required(apellido1);
		validation.required(mail);
		if (validation.hasErrors()){
			renderArgs.put("visionadmin", visionadmin);
			renderArgs.put("usuarioM", c);
			render("Application/principalAdmin.html");
		}

		Cliente comprobarusuario = Cliente.find("byUsuario", usuariofinal).first();
		Cliente comprobarcorreo = Cliente.find("byMail", mail).first();

		if (c == comprobarcorreo)
			comprobarcorreo = null;

		if (c == comprobarusuario)
			comprobarusuario = null;

		if (comprobarusuario == null && comprobarcorreo == null) {

			if (c != null) {

				if (usuarioinicial.equals(session.get("user")))
					session.put("user", usuariofinal);

				c.usuario = usuariofinal;
				c.mail = mail;
				c.nombre = nombre;
				c.apellido1 = apellido1;
				c.apellido2 = usuarioM.apellido2;
				if (c.admin == 0)
					c.admin = admin;

				if (!usuarioM.contraseña.equals(""))
					c.contraseña = usuarioM.contraseña;

				if (usuarioM.perfil != null)
					c.perfil = usuarioM.perfil;

				if (perfil == 1)
					c.perfil = null;

				c.save();
				List<Cliente> lclientes = Cliente.findAll();
				renderArgs.put("listaclientes", lclientes);
				renderArgs.put("visionadmin", visionadmin);
				String mensaje = "Usuario modificado correctamente";
				renderArgs.put("mensaje", mensaje);
			}
		}

		else {
			List<Cliente> lclientes = Cliente.findAll();
			renderArgs.put("listaclientes", lclientes);
			renderArgs.put("visionadmin", visionadmin);
			renderArgs.put("usuarioM", c);
			String error;

			if (comprobarusuario != null)
				error = "Este nombre de usuario ya está en uso";

			else
				error = "Este correo ya está en uso";

			renderArgs.put("error", error);
		}

			renderTemplate("Application/principalAdmin.html");
	}


	//VER USUARIO
	public static void verusuario(){

		if (visionadmin != 3){
			List<Cliente> lclientes = Cliente.findAll();
			renderArgs.put("listaclientes", lclientes);
			visionadmin = 3;
		}

		else
			visionadmin = 0;

		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}


	//ELIMINAR USUARIO
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

		Cliente eliminar = Cliente.findById(idusuario);
		List<Cliente> listaadmin = Cliente.find("byAdmin", 1).fetch();
		if (eliminar.admin == 1 && listaadmin.size() == 1) {
			if (listaadmin.size() == 1) {
				String error = "No se puede eliminar este usuario, ya que es el último con permisos de Administrador";
				renderArgs.put("error", error);
			}
		}

		else{

			List<Compra> compras = Compra.find("byCliente", eliminar).fetch();
			for (int i = 0; i<compras.size(); i++){
				compras.get(i).delete();
				compras.remove(i);
				i--;
			}

			if (eliminar == connected()){
				eliminar.delete();
				session.clear();
				render("Application/loginTemplate.html");
			}

			eliminar.delete();
			String mensaje = "Usuario eliminado correctamente";
			renderArgs.put("mensaje", mensaje);
		}

		List<Cliente> lclientes = Cliente.findAll();
		renderArgs.put("listaclientes", lclientes);
		renderArgs.put("visionadmin", visionadmin);
		renderTemplate ("Application/principalAdmin.html");
	}


	//AÑADIR PRENDA
	public static void añadirprenda(){
		if (visionadmin != 7)
			visionadmin = 7;

		else
			visionadmin = 0;

		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}

	public static void añadirprendaadmin(String tipo, String equipo, double precio, int año, Blob imagen){

		validation.required(equipo);
		validation.required(imagen);

		if(validation.hasErrors()){
			renderArgs.put("visionadmin", visionadmin);
			render("Application/principalAdmin.html");
		}

		if (precio>0 && año>0){

			equipo=equipo.toUpperCase();
			Prenda prenda = Prenda.find("byTipoAndEquipoAndAño", tipo, equipo, año).first();

			if (prenda != null){
				String error = "Este equipo ya está en la Base de Datos";
				renderArgs.put("error", error);
			}

			else{
				prenda = new Prenda(tipo, equipo, año, null, 0, precio, imagen);
				prenda.create();
				String mensaje = "Equipo añadido a la Base de Datos";
				renderArgs.put("mensaje", mensaje);
			}
		}

		else{
			String error = "No se han introducido todos los datos correctamente";
			renderArgs.put("error", error);
		}

		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}


	//MODIFICAR PRENDA
	public static void modificarprenda(){

		if (visionadmin != 8)
			visionadmin = 8;

		else
			visionadmin=0;

		List<Prenda> prendas = OrdenarPrendas(0);
		renderArgs.put("prendas", prendas);
		renderArgs.put("visionadmin",visionadmin);
		renderTemplate("Application/principalAdmin.html");
	}

	public static void cargardatosprenda(Long idprenda){

		Prenda PrendaM = Prenda.findById(idprenda);
		List<Prenda> prendas = OrdenarPrendas(0);
		referencia = PrendaM;
		renderArgs.put("prendas", prendas);
		renderArgs.put("prendaModificar", PrendaM);
		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}

	public static void modificarprendaadmin(String tipo, String equipo, double precio, int año, Blob imagen){

		validation.required(equipo);
		Prenda prenda = Prenda.findById(referencia.id);
		if (validation.hasErrors()){
			renderArgs.put("visionadmin", visionadmin);
			renderArgs.put("prendaModificar", prenda);
			List<Prenda> prendas = OrdenarPrendas(0);
			renderArgs.put("prendas", prendas);
			render("Application/principalAdmin.html");
		}

		List<Prenda> prendasiguales = Prenda.find("byTipoAndEquipoAndAño", prenda.tipo, prenda.equipo, prenda.año).fetch();
		equipo = equipo.toUpperCase();

		if (precio>0);

		else
			precio = prenda.precio;

		if (año > 0);

		else
			año = prenda.año;

		if (imagen == null)
			imagen = prenda.imagen;

		Prenda comprobar = Prenda.find("byTipoAndEquipoAndAño", tipo, equipo, año).first();
		if (comprobar == null || comprobar == prenda){
			for (int i = 0;i<prendasiguales.size();i++){
				Prenda p = prendasiguales.get(i);
				p.tipo = tipo;
				p.equipo = equipo;
				p.precio = precio;
				p.año = año;
				p.imagen = imagen;
				p.save();
			}

			String mensaje = "Prenda modificada correctamente";
			renderArgs.put("mensaje", mensaje);
		}

		else{
			String error = "Esta prenda ya existe en la Base de Datos";
			renderArgs.put("error", error);
			renderArgs.put("prendaModificar", prenda);
		}

		List<Prenda> prendas = OrdenarPrendas(0);
		renderArgs.put("prendas", prendas);
		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}


	//VER PRENDA
	public static void verprenda(){

		if (visionadmin != 9)
			visionadmin = 9;

		else
			visionadmin = 0;

		List<Prenda> listaprendas = OrdenarPrendas(0);
		renderArgs.put("listaprendas", listaprendas);
		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}

	public static void cargarprenda(Long idprenda){

		Prenda p = Prenda.findById(idprenda);
		List<Prenda> prendas = Prenda.find("byTipoAndEquipoAndAño", p.tipo, p.equipo, p.año).fetch();

		if (prendas.size() ==1)
			renderArgs.put("prendamostrar", p);

		else
			renderArgs.put("prendas", prendas);

		List<Prenda> listaprendas = OrdenarPrendas(0);
		renderArgs.put("listaprendas", listaprendas);
		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}


	//AÑADIR STOCK
   public static void goAddStock(){
		if (visionadmin != 5)
			visionadmin = 5;

		else
			visionadmin=0;


	   List<Prenda> prendas = OrdenarPrendas(0);
		renderArgs.put("prendas", prendas);
		renderArgs.put("visionadmin",visionadmin);
		renderTemplate("Application/principalAdmin.html");
	}

   public static void AddStock(Prenda prendaM, Long idprenda){

		if (!prendaM.talla.equals("") && prendaM.cantidadStock>0){
			prendaM.talla = prendaM.talla.toUpperCase();
			Prenda p = Prenda.findById(idprenda);
			if (p.talla == null){
				p.talla = prendaM.talla;
				p.cantidadStock = p.cantidadStock + prendaM.cantidadStock;
				p.save();
			}

			else{
				p = Prenda.find("byTipoAndEquipoAndTallaAndAño", p.tipo, p.equipo, prendaM.talla, p.año).first();

				if (p != null) {
					p.cantidadStock = p.cantidadStock + prendaM.cantidadStock;
					p.save();
				}

				else {
					p = Prenda.findById(idprenda);
					Prenda prenda = new Prenda(p.tipo, p.equipo, p.año, prendaM.talla, prendaM.cantidadStock, p.precio, p.imagen);
					prenda.create();
				}
			}
			String mensaje = "Stock añadido correctamente";
			renderArgs.put("mensaje", mensaje);
		}

	   else {

			String error = "No se han introducido todos los datos correctamente";
			renderArgs.put("error", error);
		}

	   List<Prenda> prendas = OrdenarPrendas(0);
	   renderArgs.put("prendas", prendas);
	   renderArgs.put("visionadmin", visionadmin);
	   renderTemplate("Application/principalAdmin.html");
   }


   //MODIFICAR STOCK
	public static void modificarstock(){

		if (visionadmin != 10)
			visionadmin = 10;

		else
			visionadmin = 0;

		List<Prenda> listaprendas = OrdenarPrendas(1);

		renderArgs.put("listaprendas", listaprendas);
		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}

	public static void modificarstockadmin(String talla, int cantidad){

		validation.required(talla);
		Prenda p = Prenda.findById(referencia.id);
		List<Prenda> listaprendas = new ArrayList<>();
		if (validation.hasErrors()){
			renderArgs.put("visionadmin", visionadmin);
			listaprendas = OrdenarPrendas(1);
			renderArgs.put("listaprendas", listaprendas);
			renderArgs.put("quitarprenda", p);
			render("Application/principalAdmin.html");
		}

		Prenda comprobar;
		if (cantidad > 0) {
			talla = talla.toUpperCase();
			comprobar = Prenda.find("byTipoAndEquipoAndAñoAndTalla", p.tipo, p.equipo, p.año, talla).first();

			if (cantidad > p.cantidadStock)
				cantidad = p.cantidadStock;

			if (comprobar != null) {
				comprobar.cantidadStock = comprobar.cantidadStock + cantidad;
				p.cantidadStock = p.cantidadStock - cantidad;
				comprobar.save();
				p.save();
			}
			else {
				if (cantidad == p.cantidadStock){
					p.talla = talla;
					p.save();
				}

				else{
					Prenda nueva = new Prenda(p.tipo, p.equipo, p.año, talla, cantidad, p.precio, p.imagen);
					nueva.create();
					p.cantidadStock = p.cantidadStock - cantidad;
					p.save();
				}
			}
			String mensaje = "Prenda modificada correctamente";
			renderArgs.put("mensaje", mensaje);
		}

		else{
			String error = "Has introducido una cantidad errónea";
			renderArgs.put("error", error);
		}

		listaprendas = OrdenarPrendas(1);
		renderArgs.put("listaprendas", listaprendas);
		renderArgs.put("quitarprenda", p);
		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}


   //QUITAR STOCK
   public static void cargarquitarstock(){

		if (visionadmin != 6)
			visionadmin = 6;

		else
			visionadmin = 0;

		List<Prenda> listaprendas = OrdenarPrendas(1);

		renderArgs.put("listaprendas", listaprendas);
		renderArgs.put("visionadmin", visionadmin);
	    renderTemplate("Application/principalAdmin.html");
  }
   
   public static void cargarequipo(Long idprenda) {

	   List<Prenda> listaprendas = OrdenarPrendas(1);
	   Prenda p = Prenda.findById(idprenda);
	   referencia = p;
	   renderArgs.put("listaprendas", listaprendas);
	   renderArgs.put("quitarprenda", p);
	   renderArgs.put("visionadmin", visionadmin);
	   renderTemplate("Application/principalAdmin.html");
   }

   public static void quitarStock(int cantidad) {

		Prenda p = Prenda.findById(referencia.id);

	   if (cantidad < 0) {
		   String error = "Cantidad no introducida correctamente";
		   renderArgs.put("error", error);
	   }

	   else {

		   if (cantidad > p.cantidadStock)
			   p.cantidadStock = 0;
		   else
			   p.cantidadStock = p.cantidadStock - cantidad;

		   p.save();
		   String mensaje = "Stock quitado correctamente";
		   renderArgs.put("mensaje", mensaje);
	   }

	   List<Prenda> listaprendas = OrdenarPrendas(1);
	   renderArgs.put("listaprendas", listaprendas);
	   renderArgs.put("quitarprenda", p);
	   renderArgs.put("visionadmin", visionadmin);
	   render("Application/principalAdmin.html");
   }
}