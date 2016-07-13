package objetos;

/**
 * Created by Juan on 17/12/2015.
 */
public class Archivo {
    private String id_ruta;
    private String src;
    private String estado;
    private String tipo;

    public Archivo(String id_ruta, String src, String estado, String tipo) {
        this.setId_ruta(id_ruta);
        this.setSrc(src);
        this.setEstado(estado);
        this.setTipo(tipo);
    }


    public String getId_ruta() {
        return id_ruta;
    }

    public void setId_ruta(String id_ruta) {
        this.id_ruta = id_ruta;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
