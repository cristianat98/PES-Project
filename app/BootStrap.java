import org.yaml.snakeyaml.reader.StreamReader;
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
            Prenda p1= new Prenda("CAMISETA", "BARCELONA", "M", 20, 75).save();
            Prenda p2= new Prenda("PANTALON", "MADRID", "L",50, 22.5).save();
            Prenda p3= new Prenda("PANTALON", "VALENCIA", "XL",10, 22.5).save();
            Prenda p4= new Prenda("CAMISETA", "MADRID", "M",30, 73.5).save();
            Cliente cristian = new Cliente("cristian", "cristianat98").save();
            Cliente david = new Cliente ("david", "davidp").save();
            Cliente fernando = new Cliente ("fernando", "fernandow").save();
            cristian.setAdmin(1);
            david.setAdmin(1);
            fernando.setAdmin(1);
            cristian.save();
            david.save();
            fernando.save();
        }

    }

}