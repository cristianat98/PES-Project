import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

@OnApplicationStart
public class BootStrap extends Job {

    public void doJob() {
        // Check if the database is empty
        if(Cliente.count() == 0) {
            //Fixtures.loadModels("initial-data.yml");
            Cliente admin = new Cliente("admin", "1234").save();
            admin.setAdmin(1);
            admin.save();
        }
    Prenda p1= new Prenda("Camiseta", "Barcelona", "M", 20, 75).save();
    Prenda p2= new Prenda("Pantalon", "Madrid", "L",50, 22.5).save();
    Prenda p3= new Prenda("Pantalon", "Valencia", "XL",10, 22.5).save();
    Prenda p4= new Prenda("Camiseta", "Madrid", "M",30, 73.5).save();
    Prenda p5= new Prenda("Pelota", "Español", "",10, 15).save();
    }

}