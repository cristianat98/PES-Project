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
            Prenda p1= new Prenda("CAMISETA", "BARCELONA", 2019,"M", 20, 75, null).save();
            Prenda p2= new Prenda("PANTALON", "MADRID", 2019,"L",50, 22.5, null).save();
            Prenda p3= new Prenda("PANTALON", "VALENCIA", 2019, "XL",10, 22.5, null).save();
            Prenda p4= new Prenda("CAMISETA", "MADRID", 2019,"M",30, 73.5, null).save();
            Cliente cristian = new Cliente("cristian", "cristian", "armesto", "cristianat98", "cristian@es").save();
            Cliente david = new Cliente ("david", "david", "david","davidp", "david@es").save();
            Cliente fernando = new Cliente ("fernando", "fernando","fernando","fernandop", "fernando@es").save();
            cristian.admin = 1;
            david.admin = 1;
            fernando.admin = 1;
            cristian.save();
            david.save();
            fernando.save();
        }

    }

}