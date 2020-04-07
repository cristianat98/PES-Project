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
    }

}