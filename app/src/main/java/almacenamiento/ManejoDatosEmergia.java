package almacenamiento;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import objetos.Archivo;
import objetos.Empleado;
import objetos.Ruta;
import objetos.Usuario;

/**
 * Created by Juan on 03/12/2015.
 */
public class ManejoDatosEmergia {

    private SQLiteDatabase db;
    private SQLHelperEmergia dbHelper;
    private String[] todosLosCamposRuta = {
            SQLHelperEmergia.ID_RUTA,
            SQLHelperEmergia.HORA_INICIO,
            SQLHelperEmergia.HORA_FIN,
            SQLHelperEmergia.LAT_INICIO,
            SQLHelperEmergia.LONG_INICIO,
            SQLHelperEmergia.LAT_FIN,
            SQLHelperEmergia.LONG_FIN,
            SQLHelperEmergia.TERMINADO,
            SQLHelperEmergia.NOMBRE_RUTA
    };

    private String[] todosLosCamposEmpleado = {
            SQLHelperEmergia.ID_RUTA_EMPLEADO,
            SQLHelperEmergia.CEDULA,
            SQLHelperEmergia.NOMBRE,
            SQLHelperEmergia.TELEFONO,
            SQLHelperEmergia.RECOGIDO,
            SQLHelperEmergia.FOTO,
            SQLHelperEmergia.URL_FOTO,
            SQLHelperEmergia.LATITUD,
            SQLHelperEmergia.LONGITUD,
            SQLHelperEmergia.HORA,
            SQLHelperEmergia.TIEMPO_ESPERA,
            SQLHelperEmergia.LLAMADA,
            SQLHelperEmergia.OBSERVACIONES,
            SQLHelperEmergia.TERMINADO_EMPLEADO,
            SQLHelperEmergia.DIRECCION
    };

    private String[] todosLosCamposArchivo = {
            SQLHelperEmergia.ID_RUTA_ARCHIVOS,
            SQLHelperEmergia.SRC_ARCHIVO,
            SQLHelperEmergia.ESTADO,
            SQLHelperEmergia.TIPO
    };

    public ManejoDatosEmergia(Context context) {
        dbHelper = new SQLHelperEmergia(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public void crearRuta(String id, String lat_inicio, String long_inicio, String nombre) {
        ContentValues values = new ContentValues();
        values.put(SQLHelperEmergia.ID_RUTA, id);
        values.put(SQLHelperEmergia.HORA_INICIO, getFechaActual()+" "+getHoraActual());
        values.put(SQLHelperEmergia.LAT_INICIO, lat_inicio);
        values.put(SQLHelperEmergia.LONG_INICIO, long_inicio);
        values.put(SQLHelperEmergia.NOMBRE_RUTA, nombre);
        db.insert(SQLHelperEmergia.TABLA_RUTA, null,
                values);
    }

    public void crearEmpleado(String id, String cedula, String nombre, String telefono, String direccion) {
        ContentValues values = new ContentValues();
        values.put(SQLHelperEmergia.ID_RUTA_EMPLEADO, id);
        values.put(SQLHelperEmergia.CEDULA, cedula);
        values.put(SQLHelperEmergia.NOMBRE, nombre);
        values.put(SQLHelperEmergia.TELEFONO, telefono);
        values.put(SQLHelperEmergia.DIRECCION, direccion);
        db.insert(SQLHelperEmergia.TABLA_EMPLEADO_RUTA, null,
                values);
    }

    public void crearArchivo(String id_ruta, String src, String tipo) {
        ContentValues values = new ContentValues();
        values.put(SQLHelperEmergia.ID_RUTA_ARCHIVOS, id_ruta);
        values.put(SQLHelperEmergia.SRC_ARCHIVO, src);
        values.put(SQLHelperEmergia.ESTADO, "0");
        values.put(SQLHelperEmergia.TIPO, tipo);
        db.insert(SQLHelperEmergia.TABLA_ARCHIVOS_RUTA, null,
                values);
    }

    public void finalizarRuta(String id_ruta, String lat_fin, String long_fin) {
        ContentValues valores = new ContentValues();
        valores.put(SQLHelperEmergia.HORA_FIN, getFechaActual()+" "+getHoraActual());
        valores.put(SQLHelperEmergia.LAT_FIN, lat_fin);
        valores.put(SQLHelperEmergia.LONG_FIN, long_fin);
        valores.put(SQLHelperEmergia.TERMINADO, "1");
        db.update(SQLHelperEmergia.TABLA_RUTA, valores, SQLHelperEmergia.ID_RUTA + " = " + id_ruta, null);
    }

    public void modificarEstadoArchivo(String id_ruta, String src) {
        ContentValues valores = new ContentValues();
        valores.put(SQLHelperEmergia.ESTADO, "1");
        db.update(SQLHelperEmergia.TABLA_ARCHIVOS_RUTA, valores, SQLHelperEmergia.ID_RUTA_ARCHIVOS + " = " + id_ruta
                + " and " + SQLHelperEmergia.SRC_ARCHIVO + " = " + src, null);
    }

    public void modificarRecogida(String id_ruta, String cedula, int recogido) {
        ContentValues valores = new ContentValues();
        valores.put(SQLHelperEmergia.RECOGIDO, recogido);
        valores.put(SQLHelperEmergia.HORA, getFechaActual()+" "+getHoraActual());
        db.update(SQLHelperEmergia.TABLA_EMPLEADO_RUTA, valores, SQLHelperEmergia.ID_RUTA_EMPLEADO + " = " + id_ruta
                + " and " + SQLHelperEmergia.CEDULA + " = " + cedula, null);
    }

    public void modificarCoordenadas(String id_ruta, String cedula, String latitud, String longitud) {
        ContentValues valores = new ContentValues();
        valores.put(SQLHelperEmergia.LATITUD, latitud);
        valores.put(SQLHelperEmergia.LONGITUD, longitud);
        db.update(SQLHelperEmergia.TABLA_EMPLEADO_RUTA, valores, SQLHelperEmergia.ID_RUTA_EMPLEADO + " = " + id_ruta
                + " and " + SQLHelperEmergia.CEDULA + " = " + cedula, null);
    }

    public void modificarFoto(String id_ruta, String cedula, int foto) {
        ContentValues valores = new ContentValues();
        valores.put(SQLHelperEmergia.FOTO, foto);
        db.update(SQLHelperEmergia.TABLA_EMPLEADO_RUTA, valores, SQLHelperEmergia.ID_RUTA_EMPLEADO + " = " + id_ruta
                + " and " + SQLHelperEmergia.CEDULA + " = " + cedula, null);
    }

    public void modificarUrlFoto(String id_ruta, String cedula, String url_foto) {
        ContentValues valores = new ContentValues();
        valores.put(SQLHelperEmergia.URL_FOTO, url_foto);
        db.update(SQLHelperEmergia.TABLA_EMPLEADO_RUTA, valores, SQLHelperEmergia.ID_RUTA_EMPLEADO + " = " + id_ruta
                + " and " + SQLHelperEmergia.CEDULA + " = " + cedula, null);
    }

    public void modificarTiempoEspera(String id_ruta, String cedula, String tiempo) {
        ContentValues valores = new ContentValues();
        valores.put(SQLHelperEmergia.TIEMPO_ESPERA, tiempo);
        db.update(SQLHelperEmergia.TABLA_EMPLEADO_RUTA, valores, SQLHelperEmergia.ID_RUTA_EMPLEADO + " = " + id_ruta
                + " and " + SQLHelperEmergia.CEDULA + " = " + cedula, null);
    }

    public void modificarLlamada(String id_ruta, String cedula, int llamada) {
        ContentValues valores = new ContentValues();
        valores.put(SQLHelperEmergia.LLAMADA, llamada);
        db.update(SQLHelperEmergia.TABLA_EMPLEADO_RUTA, valores, SQLHelperEmergia.ID_RUTA_EMPLEADO + " = " + id_ruta
                + " and " + SQLHelperEmergia.CEDULA + " = " + cedula, null);
    }

    public void modificarObservaciones(String id_ruta, String cedula, String observaciones) {
        ContentValues valores = new ContentValues();
        valores.put(SQLHelperEmergia.OBSERVACIONES, observaciones);
        db.update(SQLHelperEmergia.TABLA_EMPLEADO_RUTA, valores, SQLHelperEmergia.ID_RUTA_EMPLEADO + " = " + id_ruta
                + " and " + SQLHelperEmergia.CEDULA + " = " + cedula, null);
    }

    public void terminarOpcionesEmpleado(String id_ruta, String cedula) {
        ContentValues valores = new ContentValues();
        valores.put(SQLHelperEmergia.TERMINADO_EMPLEADO, 1);
        db.update(SQLHelperEmergia.TABLA_EMPLEADO_RUTA, valores, SQLHelperEmergia.ID_RUTA_EMPLEADO + " = " + id_ruta
                + " and " + SQLHelperEmergia.CEDULA + " = " + cedula, null);
    }


    /**
     * Método utilizado para saber si un empleado ya fue recogido
     *
     * @return, TRUE si ya fue recogido
     */
    public boolean validarEmpleadoRecogido(String id_ruta, String cedula) {
        Cursor cursor = db.query(SQLHelperEmergia.TABLA_EMPLEADO_RUTA,
                todosLosCamposEmpleado, SQLHelperEmergia.ID_RUTA_EMPLEADO + " = " + id_ruta + " and " +
                        SQLHelperEmergia.CEDULA + " = " + cedula
                        + " and " + SQLHelperEmergia.TERMINADO + " = 1", null,
                null, null, null);

        boolean terminado = false;
        if (cursor.moveToFirst()) {
            terminado = true;
        }
        cursor.close();
        return terminado;
    }


    /**
     * Método utilizado para saber si una ruta ya fue terminada
     *
     * @return, TRUE si ya fue terminada
     */
    public boolean validarRutaTerminada(String id_ruta) {
        Cursor cursor = db.query(SQLHelperEmergia.TABLA_RUTA,
                todosLosCamposRuta, SQLHelperEmergia.ID_RUTA + " = " + id_ruta
                        + " and " + SQLHelperEmergia.TERMINADO + " = 1", null,
                null, null, null);

        boolean terminado = false;
        if (cursor.moveToFirst()) {
            terminado = true;
        }
        cursor.close();
        return terminado;
    }

    /**
     * Método utilizado para validar si una ruta se puede terminar
     *
     * @return, TRUE si los empleados han sido recoger, FALSE si aun hay empleados por recoger
     */
    public boolean validarTerminarRuta(String id_ruta) {
        Cursor cursor = db.query(SQLHelperEmergia.TABLA_EMPLEADO_RUTA,
                todosLosCamposEmpleado, SQLHelperEmergia.ID_RUTA_EMPLEADO + " = " + id_ruta
                        + " and " + SQLHelperEmergia.TERMINADO + " = 0", null,
                null, null, null);
        boolean terminado = true;
        if (cursor.moveToFirst()) {
            terminado = false;
        }
        cursor.close();
        return terminado;
    }

    /**
     * Método utilizado para obtener los empleados asocidados a una ruta
     *
     * @param id_ruta, id de la ruta
     * @return, lista de empleados
     */
    public ArrayList<Empleado> getEmpleadosRuta(String id_ruta) {
        ArrayList<Empleado> empleados = new ArrayList<Empleado>();
        Cursor cursor = db.query(SQLHelperEmergia.TABLA_EMPLEADO_RUTA,
                todosLosCamposEmpleado, SQLHelperEmergia.ID_RUTA_EMPLEADO + " = " + id_ruta, null,
                null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                Empleado empleado = new Empleado(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10),
                        cursor.getString(11),
                        cursor.getString(12),
                        cursor.getString(13),
                        cursor.getString(14)
                );
                empleados.add(empleado);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return empleados;
    }


    /**
     * Método utilizado para obtener los archivos que deben subirse al servidor
     *
     * @return, lista de archivos IMG que deben subirse
     */
    public ArrayList<Archivo> getArchivosIMGParaSubir() {
        ArrayList<Archivo> archivos = new ArrayList<Archivo>();
        Cursor cursor = db.query(SQLHelperEmergia.TABLA_ARCHIVOS_RUTA,
                todosLosCamposArchivo, SQLHelperEmergia.ESTADO + " = 0" +
                        " and " + SQLHelperEmergia.TIPO + " = 'IMG'", null,
                null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                Archivo archivo = new Archivo(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
                archivos.add(archivo);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return archivos;
    }


    /**
     * Método utilizado para obtener los archivos que deben subirse al servidor
     *
     * @return, lista de archivos JSON que deben subirse
     */
    public ArrayList<Archivo> getArchivosJSONParaSubir() {
        ArrayList<Archivo> archivos = new ArrayList<Archivo>();
        Cursor cursor = db.query(SQLHelperEmergia.TABLA_ARCHIVOS_RUTA,
                todosLosCamposArchivo, SQLHelperEmergia.ESTADO + " = 0" +
                        " and " + SQLHelperEmergia.TIPO + " = 'JSON'", null,
                null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                Archivo archivo = new Archivo(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
                archivos.add(archivo);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return archivos;
    }

    /**
     * Método utilizado para obtener todas las rutas
     *
     * @return, lista de rutas
     */
    public ArrayList<Ruta> getTodasLasRutas() {
        ArrayList<Ruta> rutas = new ArrayList<Ruta>();

        Cursor cursor = db.query(SQLHelperEmergia.TABLA_RUTA,
                todosLosCamposRuta, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Ruta ruta = new Ruta(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8));
                ruta.setEmpleados(getEmpleadosRuta(ruta.getId_ruta()));
                rutas.add(ruta);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return rutas;
    }

    /**
     * Método utilizado para obtener la información de la ruta
     *
     * @return, información de la ruta solicitada
     */
    public Ruta getRuta(String id_ruta) {
        Ruta ruta = null;
        Cursor cursor = db.query(SQLHelperEmergia.TABLA_RUTA,
                todosLosCamposRuta, SQLHelperEmergia.ID_RUTA + " = " + id_ruta, null, null, null, null);

        if (cursor.moveToFirst()) {
            ruta = new Ruta(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8));
            ruta.setEmpleados(getEmpleadosRuta(ruta.getId_ruta()));
        }
        cursor.close();
        return ruta;
    }

    /**
     * Método utilizado para obtener la fecha actual en formato DD-MM-YYYY
     *
     * @return fecha actual
     */
    public String getFechaActual() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = df.format(c.getTime());
        return fecha;
    }


    /**
     * Método utilizado para obtener la hora actual en formato HH:MM:SS
     *
     * @return hora actual
     */
    public String getHoraActual() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm:ss");
        String hora = sdf.format(now);
        String[] valores = hora.split(":");
        if (valores[0].equals("24")) {
            valores[0] = "00";
        }
        return valores[0] + ":" + valores[1] + ":" + valores[2];
    }
}
