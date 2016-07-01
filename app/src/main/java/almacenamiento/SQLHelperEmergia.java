package almacenamiento;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Juan on 03/12/2015.
 */
public class SQLHelperEmergia extends SQLiteOpenHelper {

    //TABLA RUTA
    public static final String TABLA_RUTA = "ruta";
    public static final String ID_RUTA = "_id_ruta";
    public static final String HORA_INICIO = "_hora_inicio";
    public static final String HORA_FIN = "_hora_fin";
    public static final String LAT_INICIO = "_lat_inicio";
    public static final String LONG_INICIO = "_long_inicio";
    public static final String LAT_FIN = "_lat_fin";
    public static final String LONG_FIN = "_long_fin";
    public static final String TERMINADO = "_terminado";
    public static final String NOMBRE_RUTA = "_nombre";

    //TABLA EMPLEADO_RUTA
    public static final String TABLA_EMPLEADO_RUTA = "empleado_ruta";
    public static final String ID_RUTA_EMPLEADO = "_id_ruta";
    public static final String CEDULA = "_cedula";
    public static final String NOMBRE = "_nombre";
    public static final String TELEFONO = "_telefono";
    public static final String RECOGIDO = "_llegada";
    public static final String FOTO = "_foto";
    public static final String URL_FOTO = "_url_foto";
    public static final String LATITUD = "_latitud";
    public static final String LONGITUD = "_longitud";
    public static final String HORA = "_hora";
    public static final String TIEMPO_ESPERA = "_tiempo_espera";
    public static final String LLAMADA = "_llamada";
    public static final String OBSERVACIONES = "_observaciones";
    public static final String TERMINADO_EMPLEADO = "_terminado";//Campo usado para saber si el conductor
    //ya marco las opciones para este empleado, ya que  recogido puede ser false o true, si este campo
    //se encuentra en true quiere decir que ya terminamos las opciones para el
    public static final String DIRECCION = "_direccion";


    //TABLA EMPLEADO_RUTA
    public static final String TABLA_ARCHIVOS_RUTA = "archivo_ruta";
    public static final String ID_RUTA_ARCHIVOS = "_id_ruta";
    public static final String SRC_ARCHIVO = "_src";
    public static final String ESTADO = "_estado";
    public static final String TIPO = "_tipo";

    //Base de datos
    private static final String NOMBRE_DB = "emergia.db";
    private static final int VERSION_DB = 1;


    private static final String QUERY_CREACION_RUTA = "create table "
            + TABLA_RUTA +
            "(" + ID_RUTA + " text primary key ," +
            "" + HORA_INICIO + " datetime," +
            "" + HORA_FIN + " datetime," +
            "" + LAT_INICIO + " text," +
            "" + LONG_INICIO + " text," +
            "" + LAT_FIN + " text," +
            "" + LONG_FIN + " text," +
            "" + TERMINADO + " INTEGER DEFAULT 0," +
            "" + NOMBRE_RUTA + " text);";


    private static final String QUERY_CREACION_EMPLEADO = "create table "
            + TABLA_EMPLEADO_RUTA +
            "(" + ID_RUTA_EMPLEADO + " text," +
            "" + CEDULA + " text," +
            "" + NOMBRE + " text," +
            "" + TELEFONO + " text," +
            "" + RECOGIDO + " INTEGER DEFAULT 0," +
            "" + FOTO + " INTEGER DEFAULT 0," +
            "" + URL_FOTO + " text," +
            "" + LATITUD + " text," +
            "" + LONGITUD + " text," +
            "" + HORA + " datetime," +
            "" + TIEMPO_ESPERA + " text," +
            "" + LLAMADA + " INTEGER DEFAULT 0," +
            "" + OBSERVACIONES + " text," +
            "" + TERMINADO_EMPLEADO + " INTEGER DEFAULT 0," +
            "" + DIRECCION + " text," +
            "PRIMARY KEY (" + ID_RUTA_EMPLEADO + ", " + CEDULA + ")," +
            "FOREIGN KEY (" + ID_RUTA_EMPLEADO + ") REFERENCES " + TABLA_RUTA + " (" + ID_RUTA + "));";

    private static final String QUERY_CREACION_ARCHIVO_RUTA = "create table "
            + TABLA_ARCHIVOS_RUTA +
            "(" + ID_RUTA_ARCHIVOS + " text," +
            "" + SRC_ARCHIVO + " text," +
            "" + ESTADO + " INTEGER DEFAULT 0," +
            "" + TIPO + " text," +
            "PRIMARY KEY (" + ID_RUTA_ARCHIVOS + ", " + SRC_ARCHIVO + ")," +
            "FOREIGN KEY (" + ID_RUTA_ARCHIVOS + ") REFERENCES " + TABLA_RUTA + " (" + ID_RUTA + "));";


    public SQLHelperEmergia(Context context) {
        super(context, NOMBRE_DB, null, VERSION_DB);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_CREACION_RUTA);
        db.execSQL(QUERY_CREACION_EMPLEADO);
        db.execSQL(QUERY_CREACION_ARCHIVO_RUTA);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_EMPLEADO_RUTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_RUTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_ARCHIVOS_RUTA);
        onCreate(db);
    }
}
