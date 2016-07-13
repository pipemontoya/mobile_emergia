package objetos;

/**
 * Created by Juan on 03/12/2015.
 */
public class Empleado {

    private String cedula;
    private String nombre;
    private String telefono;
    private String recogido;
    private String foto;
    private String url_foto;
    private String latitud;
    private String longitud;
    private String hora;
    private String tiempo_espera;
    private String llamada;
    private String observaciones;
    private String terminado;
    private String direccion;

    public Empleado(String cedula, String nombre, String telefono, String recogido, String foto, String url_foto, String latitud, String longitud, String hora, String tiempo_espera, String llamada, String observaciones, String terminado, String direccion) {
        this.setCedula(cedula);
        this.setNombre(nombre);
        this.setTelefono(telefono);
        this.setRecogido(recogido);
        this.setFoto(foto);
        this.setUrl_foto(url_foto);
        this.setLatitud(latitud);
        this.setLongitud(longitud);
        this.setHora(hora);
        this.setTiempo_espera(tiempo_espera);
        this.setLlamada(llamada);
        this.setObservaciones(observaciones);
        this.setTerminado(terminado);
        this.setDireccion(direccion);
    }


    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String isRecogido() {
        return recogido;
    }

    public void setRecogido(String recogido) {
        this.recogido = recogido;
    }

    public String isFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getUrl_foto() {
        return url_foto;
    }

    public void setUrl_foto(String url_foto) {
        this.url_foto = url_foto;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTiempo_espera() {
        return tiempo_espera;
    }

    public void setTiempo_espera(String tiempo_espera) {
        this.tiempo_espera = tiempo_espera;
    }

    public String isLlamada() {
        return llamada;
    }

    public void setLlamada(String llamada) {
        this.llamada = llamada;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String isTerminado() {
        return getTerminado();
    }

    public void setTerminado(String terminado) {
        this.terminado = terminado;
    }


    public String getTerminado() {
        return terminado;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
