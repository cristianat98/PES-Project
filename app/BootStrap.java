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
            Prenda p1= new Prenda("CAMISETA", "BARCELONA", 2019,"M", 20, 75).save();
            Prenda p2= new Prenda("PANTALON", "MADRID", 2019,"L",50, 22.5).save();
            Prenda p3= new Prenda("PANTALON", "VALENCIA", 2019, "XL",10, 22.5).save();
            Prenda p4= new Prenda("CAMISETA", "MADRID", 2019,"M",30, 73.5).save();
            Cliente cristian = new Cliente("cristian", "cristianat98").save();
            Cliente david = new Cliente ("david", "davidp").save();
            Cliente fernando = new Cliente ("fernando", "fernandow").save();
            cristian.admin = 1;
            david.admin = 1;
            fernando.admin = 1;
            cristian.save();
            david.save();
            fernando.save();
        }

    }

}