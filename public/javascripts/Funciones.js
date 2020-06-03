var x = 0;
function Modificarusuario(){
    document.getElementById('ModificarUsuario').style.display = "block";
    document.getElementById('AñadirUsuario').style.display = "none";
    document.getElementById('DatosUsuario').style.display = "none";
}

function CargarDatos(){
    document.getElementById('DatosUsuario').style.display = "block";
    var element = document.getElementById('Nombreusuarios').selectedIndex;
    var selected = document.getElementById('Nombreusuarios');
    var nodelist = document.getElementById("Nombreusuarios").item(0).innerHTML;
    alert(nodelist);
    document.getElementsByName('username').value = selected[element].value;
    document.getElementById('usuario').placeholder = selected[element].value;
}
