package poo2.progs.servicios;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("servicios")
public class AplicacionConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> recursos = new java.util.HashSet<>();
        agregaRecursosREST(recursos);
        return recursos;
    }

    /**
     * Este metodo agrega al argumento recibido las clases de servicios
     * REST que estaran disponibles en el proyecto
     * @param recursos Set de objetos Class donde deben agregarse las clases de servicio
     */
    private void agregaRecursosREST(Set<Class<?>> recursos) {
        // TODO Agrega una linea recursos.add(NombreClaseREST.class) por cada nuevo servicio REST
    }
}