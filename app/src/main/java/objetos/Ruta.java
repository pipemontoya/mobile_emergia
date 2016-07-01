package objetos;

import java.util.ArrayList;

/**
 * Created by Juan on 03/12/2015.
 */
public class Ruta {
    private String id_ruta;
    private String hora_inicio;
    private String hora_fin;
    private String lat_inicio;
    private String long_inicio;
    private String lat_fin;
    private String long_fin;
    private String terminado;
    private ArrayList<Empleado> empleados;
    private String nombre;

    public Ruta(String id_ruta, String hora_inicio, String hora_fin, String lat_inicio, String long_inicio, String lat_fin, String long_fin, String terminado, String nombre) {
        this.setId_ruta(id_ruta);
        this.setHora_inicio(hora_inicio);
        this.setHora_fin(hora_fin);
        this.setLat_inicio(lat_inicio);
        this.setLong_inicio(long_inicio);
        this.setLat_fin(lat_fin);
        this.setLong_fin(long_fin);
        this.setTerminado(terminado);
        this.setEmpleados(new ArrayList<Empleado>());
        this.setNombre(nombre);
    }


    public String getId_ruta() {
        return id_ruta;
    }

    public void setId_ruta(String id_ruta) {
        this.id_ruta = id_ruta;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    public String getLat_inicio() {
        return lat_inicio;
    }

    public void setLat_inicio(String lat_inicio) {
        this.lat_inicio = lat_inicio;
    }

    public String getLong_inicio() {
        return long_inicio;
    }

    public void setLong_inicio(String long_inicio) {
        this.long_inicio = long_inicio;
    }

    public String getLat_fin() {
        return lat_fin;
    }

    public void setLat_fin(String lat_fin) {
        this.lat_fin = lat_fin;
    }

    public String getLong_fin() {
        return long_fin;
    }

    public void setLong_fin(String long_fin) {
        this.long_fin = long_fin;
    }

    public String isTerminado() {
        return terminado;
    }

    public void setTerminado(String terminado) {
        this.terminado = terminado;
    }


    public ArrayList<Empleado> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(ArrayList<Empleado> empleados) {
        this.empleados = empleados;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
