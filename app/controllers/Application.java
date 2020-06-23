
package controllers;

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

	static List<Prenda> OrdenarPrendas(){

		List<Prenda> prendas = Prenda.findAll();

		for(int i = 0; i<prendas.size();i++){
			for (int j = i+1; j<prendas.size(); j++){
				if (prendas.get(i).tipo.equals(prendas.get(j).tipo) && prendas.get(i).equipo.equals(prendas.get(j).equipo) && prendas.get(i).año == prendas.get(j).año){
					prendas.remove(j);
					j--;
				}
			}
		}

		List<Integer> años = new ArrayList<Integer>();
		List<String> equipos = new ArrayList<String>();

		for (int i = 0; i<prendas.size();i++){
			equipos.add(prendas.get(i).equipo);
			años.add(prendas.get(i).año);
		}

		List<Prenda> listaprendas = new ArrayList<Prenda>();
		Collections.sort(años);

		for(int i = 0;i<años.size();i++){
			for(int j = 0;j<prendas.size();j++){
				if (años.get(i) == prendas.get(j).año){
					listaprendas.add(prendas.get(j));
					prendas.remove(j);
					j--;
				}
			}
		}

		prendas = listaprendas;
		listaprendas = new ArrayList<>();
		Collections.sort(equipos);

		for(int i = 0;i<equipos.size();i++){
			for(int j = 0;j<prendas.size();j++){
				if (equipos.get(i).equals(prendas.get(j).equipo)){
					listaprendas.add(prendas.get(j));
					prendas.remove(j);
					j--;
				}
			}
		}

		return listaprendas;
	}

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

	//INDEX
	public static void index() {

		if(connected() != null) {
			List<Prenda> camisetas =Prenda.find("byTipo", "CAMISETA").fetch();
			for (int i = 0; i < camisetas.size(); i++){
				if (camisetas.get(i).cantidadStock == 0){
					camisetas.remove(i);
					i--;
				}
			}

			List<Prenda> pantalones =Prenda.find("byTipo", "PANTALON").fetch();
			for (int i = 0; i < pantalones.size(); i++){
				if (pantalones.get(i).cantidadStock == 0){
					pantalones.remove(i);
					i--;

				}
			}

			renderArgs.put("camisetas",camisetas);
			renderArgs.put("pantalones",pantalones);
			render("Application/principal.html");
		}
		else {
			renderTemplate("Application/loginTemplate.html");
		}
	}


	//ACCIONES PRIMERA VEZ

	//ANDROID
	public static void registrarAndroid(String user, String password) {
		Cliente c = Cliente.find("byUsuario",user).first();
		if(c==null) {
			String mail = user + "@es";
			c= new Cliente(user,user, user, password, mail);
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


	//SERVIDOR WEB
	public static void Login(@Valid Cliente cliente, String adm) {

		Cliente c = Cliente.find("byUsuarioAndContraseña", cliente.usuario, cliente.contraseña).first();
		if (c != null) {
			if (adm.equals("Entrar Como Administrador")) {
				if (c.admin == 0) {
					String erroradmin = "erroradmin";
					renderArgs.put("erroradmin", erroradmin);
					renderTemplate("Application/loginTemplate.html");
				}

				else{
					session.put("user",cliente.usuario);
					renderArgs.put("client", c);
					renderArgs.put("visionadmin", visionadmin);
					renderArgs.put("client", c);
					renderTemplate("Application/principalAdmin.html");
				}
			}

			else{
				session.put("user",cliente.usuario);
				renderArgs.put("client", c);
				index();
			}

		}
		else{
			String error = "error";
			renderArgs.put("error", error);
			renderTemplate("Application/loginTemplate.html");
		}
	}

	public static void loginTemplate(){
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
		if(validation.hasErrors())
			render("@register");


		Cliente c = Cliente.find("byUsuario", usuario).first();
		if (c == null) {
			nuevocliente.usuario = usuario;
			nuevocliente.mail = mail;
			nuevocliente.nombre = nombre;
			nuevocliente.apellido1 = apellido1;
			nuevocliente.create();
			session.put("user", nuevocliente.usuario);
			index();
		}

		else{
			validation.equals(usuario, "").message("El usuario ya está en uso");
			render("@register");
		}
	}

	public static void recuperacionContra() {
		render();
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


	//CARGAR PÁGINA
	public static void MostrarPrenda(Long id){

		Prenda prenda = Prenda.findById(id);
		renderBinary(prenda.imagen.get());
	}

	public static void MostrarPerfil(int num) {

		Cliente c;

		if (num == 1)
			c = Cliente.find("byUsuario", session.get("user")).first();

		else
			c = visualizar;

		c.perfil.toString();
		if (c.perfil.get() == null)
			renderBinary(new File("C:/Users/cristian/Desktop/play-1.5.3/Proyecto-PES/public/images/avatar.jpg"));

		else
			renderBinary(c.perfil.get());
	}


	//ACCIONES USUARIO

	//MODIFICAR PERFIL
	public static void ModificarUsuario() {
		renderArgs.put("modificar", 0);
		render();
	}

	public static void cargardatosusuario(String contraseña){
		int i = 0;
		validation.required(contraseña);
		String username = session.get("user");
		Cliente usuario=Cliente.find("byUsuario", username).first();
		validation.equals(contraseña, usuario.contraseña).message("La contraseña es incorrecta, no puede modificar datos");
		renderArgs.put("modificar", i);
		if(validation.hasErrors()) {
			render("@ModificarUsuario");
		}
		else{
			renderArgs.put("modificar", 1);
			renderArgs.put("clienteM", usuario);   //Variable en HTML , Variable en java//
			renderTemplate("Application/ModificarUsuario.html");
		}
	}

	public static void ModificarDatos2(Cliente clienteM, String contraseña) {

		String username = session.get("user");
		Cliente c=Cliente.find("byUsuario", username).first();
		validation.equals(contraseña, clienteM.contraseña).message("Las contraseñas no coinciden");
		if(validation.hasErrors()) {
			int i = 1;
			renderArgs.put("clienteM", c);
			renderArgs.put("modificar", 1);
			render("@ModificarUsuario");
		}

		Cliente comprobarusuario = null;
		Cliente comprobarcorreo = null;
		if (!clienteM.usuario.equals(""))
			comprobarusuario = Cliente.find("byUsuario", clienteM.usuario).first();

		if (!clienteM.mail.equals(""))
			comprobarcorreo = Cliente.find("byMail", clienteM.mail).first();

		String error = null;
		if(comprobarcorreo == null && comprobarusuario == null) {
			if (!clienteM.usuario.equals("")){
				c.usuario=clienteM.usuario;
				session.put("user", c.usuario);
			}

			if (!clienteM.contraseña.equals(""))
				c.contraseña=clienteM.contraseña;

			if (!clienteM.mail.equals(""))
				c.mail=clienteM.mail;

			if (!clienteM.nombre.equals(""))
				c.nombre=clienteM.nombre;

			if (!clienteM.apellido1.equals(""))
				c.apellido1=clienteM.apellido1;

			if (!clienteM.apellido2.equals(""))
				c.apellido2=clienteM.apellido2;

			if (clienteM.perfil != null)
				c.perfil = clienteM.perfil;

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

				else
					c.delete();
			}

			else
				c.delete();

			session.clear();
			renderTemplate("Application/loginTemplate.html");
		}
	}


	//CERRAR SESIÓN
	public static void Logout (){
		session.clear();
		visionadmin = 0;
		//renderArgs.put("client",null);
		renderTemplate("Application/loginTemplate.html");
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

	public static void ModificarU(Cliente usuarioM, String usuarioinicial, String usuariofinal, String mailinicial, String mailfinal, int admin){

		Cliente comprobarusuario = null;
		Cliente comprobarcorreo = null;
		if (!usuarioinicial.equals(usuariofinal))
			comprobarusuario = Cliente.find("byUsuario", usuariofinal).first();

		if (!mailinicial.equals(mailfinal))
			comprobarcorreo = Cliente.find("byMail", mailfinal).first();


		if (comprobarusuario == null && comprobarcorreo == null) {

			Cliente c = Cliente.find("byUsuario", usuarioinicial).first();
			if (c != null) {
				if (!usuariofinal.equals("")) {
					if (usuarioinicial.equals(session.get("user")))
						session.put("user", usuariofinal);

					c.usuario = usuariofinal;
				}

				if (!usuarioM.contraseña.equals(""))
					c.contraseña = usuarioM.contraseña;

				if (!mailfinal.equals(""))
					c.mail = mailfinal;

				if (!usuarioM.nombre.equals(""))
					c.nombre = usuarioM.nombre;

				if (!usuarioM.apellido1.equals(""))
					c.apellido1 = usuarioM.apellido1;

				if (!usuarioM.apellido2.equals(""))
					c.apellido2 = usuarioM.apellido2;

				if (usuarioM.perfil != null)
					c.perfil = usuarioM.perfil;

				c.admin = admin;

				c.save();
				List<Cliente> lclientes = Cliente.findAll();
				renderArgs.put("listaclientes", lclientes);
				renderArgs.put("usuarioM", c);
				renderArgs.put("visionadmin", visionadmin);
				String mensaje = "Usuario modificado correctamente";
				renderArgs.put("mensaje", mensaje);
			}
		}

		else {
			List<Cliente> lclientes = Cliente.findAll();
			renderArgs.put("listaclientes", lclientes);
			renderArgs.put("visionadmin", visionadmin);
			Cliente c = Cliente.find("byUsuario", usuarioinicial).first();
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

		List<Prenda> prendas = OrdenarPrendas();
		renderArgs.put("prendas", prendas);
		renderArgs.put("visionadmin",visionadmin);
		renderTemplate("Application/principalAdmin.html");
	}

	public static void cargardatosprenda(Long idprenda){

		Prenda PrendaM = Prenda.findById(idprenda);
		List<Prenda> prendas = OrdenarPrendas();
		referencia = PrendaM;
		renderArgs.put("prendas", prendas);
		renderArgs.put("prendaModificar", PrendaM);
		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}

	public static void modificarprendaadmin(String tipo, String equipo, double precio, int año, Blob imagen){

		Prenda prenda = Prenda.findById(referencia.id);
		List<Prenda> prendasiguales = Prenda.find("byTipoAndEquipoAndAño", prenda.tipo, prenda.equipo, prenda.año).fetch();

		if (!equipo.equals(""))
			equipo = equipo.toUpperCase();

		else
			equipo = prenda.equipo;

		if (precio>0)
			precio = precio;

		else
			precio = prenda.precio;

		if (año > 0)
			año = año;

		else
			año = prenda.año;

		if (imagen == null)
			imagen = prenda.imagen;

		Prenda comprobar = Prenda.find("byTipoAndEquipoAndAño", tipo, equipo, año).first();
		if (comprobar == null){
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

		List<Prenda> prendas = OrdenarPrendas();
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

		List<Prenda> listaprendas = OrdenarPrendas();
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

		List<Prenda> listaprendas = OrdenarPrendas();
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


	   List<Prenda> prendas = OrdenarPrendas();
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

	   List<Prenda> prendas = OrdenarPrendas();
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

		List<Prenda> listaprenda = OrdenarPrendas();
		List<Prenda> listaprendas = new ArrayList<>();

		for (int i = 0; i<listaprenda.size();i++){
			Prenda p = listaprenda.get(i);
			List<Prenda> prendax = Prenda.find("byTipoAndEquipoAndAño", p.tipo, p.equipo, p.año).fetch();
			for (int j = 0;j<prendax.size();j++){
				if (prendax.get(j).talla != null)
					listaprendas.add(prendax.get(j));
			}
		}

		renderArgs.put("listaprendas", listaprendas);
		renderArgs.put("visionadmin", visionadmin);
		render("Application/principalAdmin.html");
	}

	public static void modificarstockadmin(String talla){

		validation.required(talla);
		if (validation.hasErrors()){
			renderArgs.put("visionadmin", visionadmin);
			render("Application/principalAdmin.html");
		}

		talla = talla.toUpperCase();
		Prenda p = Prenda.findById(referencia.id);
		Prenda comprobar = Prenda.find("byTipoAndEquipoAndAñoAndTalla", p.tipo, p.equipo, p.año, talla).first();

		if (comprobar != null){
			String error = "Esta prenda ya existe";
			renderArgs.put("error", error);
		}

		else{
			p.talla = talla;
			p.save();
			String mensaje = "Prenda modificada correctamente";
			renderArgs.put("mensaje", mensaje);
		}

		List<Prenda> listaprenda = OrdenarPrendas();
		List<Prenda> listaprendas = new ArrayList<>();

		for (int i = 0; i < listaprenda.size(); i++) {
			Prenda prenda = listaprenda.get(i);
			List<Prenda> prendax = Prenda.find("byTipoAndEquipoAndAño", prenda.tipo, prenda.equipo, prenda.año).fetch();
			for (int j = 0; j < prendax.size(); j++) {
				if (prendax.get(j).talla != null)
					listaprendas.add(prendax.get(j));
			}
		}
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

		List<Prenda> listaprenda = OrdenarPrendas();
		List<Prenda> listaprendas = new ArrayList<>();

		for (int i = 0; i<listaprenda.size();i++){
			Prenda p = listaprenda.get(i);
			List<Prenda> prendax = Prenda.find("byTipoAndEquipoAndAño", p.tipo, p.equipo, p.año).fetch();
			for (int j = 0;j<prendax.size();j++){
				if (prendax.get(j).talla != null)
					listaprendas.add(prendax.get(j));
			}
		}

		renderArgs.put("listaprendas", listaprendas);
		renderArgs.put("visionadmin", visionadmin);
	    renderTemplate("Application/principalAdmin.html");
  }
   
   public static void cargarequipo(Long idprenda) {

	   List<Prenda> listaprenda = OrdenarPrendas();
	   List<Prenda> listaprendas = new ArrayList<>();

	   for (int i = 0; i < listaprenda.size(); i++) {
		   Prenda p = listaprenda.get(i);
		   List<Prenda> prendax = Prenda.find("byTipoAndEquipoAndAño", p.tipo, p.equipo, p.año).fetch();
		   for (int j = 0; j < prendax.size(); j++) {
		   	if (prendax.get(j).talla != null)
			   listaprendas.add(prendax.get(j));
		   }
	   }

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

	   List<Prenda> listaprenda = OrdenarPrendas();
	   List<Prenda> listaprendas = new ArrayList<>();

	   for (int i = 0; i < listaprenda.size(); i++) {
		   Prenda prenda = listaprenda.get(i);
		   List<Prenda> prendax = Prenda.find("byTipoAndEquipoAndAño", prenda.tipo, prenda.equipo, prenda.año).fetch();
		   for (int j = 0; j < prendax.size(); j++) {
			   if (prendax.get(j).talla != null)
			   	listaprendas.add(prendax.get(j));
		   }
	   }

	   renderArgs.put("listaprendas", listaprendas);
	   renderArgs.put("quitarprenda", p);
	   renderArgs.put("visionadmin", visionadmin);
	   render("Application/principalAdmin.html");
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