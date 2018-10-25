package com.cryoingdevs.init;

import com.cryoingdevs.services.MapsServices;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IvànAlejandro on 20/10/2018.
 */
public class MainApp extends Application{
    HashSet<Object> singletons = new HashSet<Object>();

    public MainApp(@Context ServletContext servletContext){
        singletons.add(new MapsServices(servletContext));
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
