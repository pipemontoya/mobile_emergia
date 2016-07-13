package objetos;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import almacenamiento.ManejoDatosEmergia;

/**
 * Created by Juan on 19/11/2015.
 */
public class Usuario {
    private Context context;
    private int codigoRespuestaServidor = 0;

    /**
     * Constructor clase usuario
     *
     * @param context, contexto o actividad actual
     */
    public Usuario(Context context) {
        this.context = context;
    }

    /**
     * Método utilizado para verificar si el GPS del dispositivo esta encendido
     *
     * @return, True si el GPS esta encendido, FALSE si el GPS esta apagado
     */
    public boolean verificarGPS() {
        LocationManager lm = null;
        boolean gps_enabled = false;
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            gps_enabled = true;
        }
        return gps_enabled;
    }


    /**
     * Método utilizado para generar una alerta indicando al usuario si desea encender el GPS
     */
    public void preguntaHabilitarGPS() {
        final AlertDialog.Builder confirmar = new AlertDialog.Builder(
                context);
        confirmar.setTitle("GPS apagado");
        confirmar.setMessage("La aplicación no funcionara correctamente si el GPS de tu dispositivo esta apagago. Por favor enciende el GPS de tu dispositivo!!");
        confirmar.setPositiveButton("Configuracion",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(myIntent);
                    }
                });
        confirmar.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        confirmar.show();
    }

    /**
     * Método utilizado para verificar si el usuario tiene conexión a internet
     *
     * @return. TRUE si tiene conexión, FALSE si no hay conexión
     */
    public boolean verificaConexion() {
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        for (int i = 0; i < 2; i++) {
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                bConectado = true;
            }
        }
        return bConectado;
    }

    /**
     * Método utilizado para almacenar un valor en un archivo
     *
     * @param nombre_archivo, Nombre del archivo a guardar
     * @param valor,          Valor del archivo
     */
    public void guardar(String nombre_archivo, String valor) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/emergia");
            myDir.mkdirs();
            String fname = nombre_archivo;
            File file = new File(myDir, fname);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(valor.getBytes());
            fos.close();
        } catch (Exception e) {
        }

    }


    /**
     * Método utilizado para obtener el valor de un archivo
     *
     * @param nombre_archivo, Nombre del archivo
     * @return valor del archivo
     */
    public String obtener(String nombre_archivo) {
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/emergia");
            myDir.mkdirs();
            String fname = nombre_archivo;
            File file = new File(myDir, fname);
            FileInputStream fIn = new FileInputStream(file);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));
            String aDataRow = "";
            String aBuffer = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow;
            }
            myReader.close();
            return aBuffer;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Método utilizado para obtener el device_id del dispositivo
     *
     * @return, Device_id del dispositivo
     */
    public String getDeviceId() {
        String device_id = "";
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        device_id = telephonyManager.getDeviceId();
        if (device_id == null) {
            device_id = android.os.Build.SERIAL;
        }
        return device_id;
    }

    /**
     * Método utilizado para mostrar una notificación TOAST en una interfaz especifica
     *
     * @param mensaje, Mensaje a mostrar
     */
    public void mostrarToast(String mensaje) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
    }

    /**
     * Este Método se utiliza para validar si un String no esta vacio
     *
     * @param texto, Texto a validar
     * @return True String valido, False String vacio
     */
    public boolean validarCampo(String texto) {
        if (!texto.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Método utilizado para obtener el valor ingresado en un EditText
     *
     * @param editText, EditText a analizar
     * @return Valor que contiene el EditText
     */
    public String obtenerTextoEditText(EditText editText) {
        return editText.getText().toString();
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

    /**
     * Método utilizado para hacer vibrar el dispositivo
     *
     * @param tiempo_vibracion, Tiempo que durara la vibracion
     */
    public void vibrar(int tiempo_vibracion) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(tiempo_vibracion);
    }

    /**
     * Método utilizado para almacenar la sesión del usuario
     *
     * @param cedula, cedula del usuario
     * @param placa,  placa del vehiculo
     * @param nombre, nombre del usuario
     */
    public void guardarSesion(String cedula, String placa, String nombre) {
        guardar("cedula", cedula);
        guardar("placa", placa);
        guardar("nombre", nombre);
    }

    /**
     * Método utilizado para eliminar la sesión del usuario
     */
    public void cerrarSesion() {
        guardar("cedula", "");
        guardar("placa", "");
        guardar("nombre", "");
    }

    /**
     * Método utilizado para verificar si el usuario tiene una sesión activa en el dispositivo
     *
     * @return, True si hay sesión activa, False si no hay sesión
     */
    public boolean verificarSesion() {
        if (!obtener("cedula").isEmpty() && !obtener("placa").isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Método utilizado para llamar a un telefono especifico
     *
     * @param telefono, telefono a llamar
     */
    public void llamar(String telefono) {
        Intent llamada = new Intent(Intent.ACTION_CALL);
        llamada.setData(Uri.parse("tel:" + telefono));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        context.startActivity(llamada);
    }

    /**
     * Método utilizado para verificar si hay una ruta activa sin finalizar
     *
     * @return, TRUE si hay ruta activa, FALSE si no hay ruta activa
     */
    public boolean verificarRutaActiva() {
        if (!obtener("ruta").isEmpty()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Método utilizado para iniciar una nueva ruta
     *
     * @param id_ruta, id de la ruta a iniciar
     */
    public void iniciarRuta(String id_ruta, String nombre) {
        guardar("ruta", id_ruta);
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.crearRuta(id_ruta, obtener("latitud"), obtener("longitud"), nombre);
        manejoDatos.close();
    }

    /**
     * Método utilizado para crear un archivo en la base de datos
     *
     * @param id_ruta, id de la ruta a iniciar
     * @param src,     url del archivo
     * @param tipo,    tipo del archivo
     */
    public void insertarArchivo(String id_ruta, String src, String tipo) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.crearArchivo(id_ruta, src, tipo);
        manejoDatos.close();
    }

    /**
     * Método utilizado para adicionar un empleado a una ruta especifica
     *
     * @param id_ruta,  id de la ruta
     * @param cedula,   cedula de la persona
     * @param nombre,   nombre de la persona
     * @param telefono, telefono de la persona
     */
    public void adicionarEmpleadoRuta(String id_ruta, String cedula, String nombre, String telefono, String direccion) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.crearEmpleado(id_ruta, cedula, nombre, telefono, direccion);
        manejoDatos.close();
    }

    /**
     * Método utilizado para verificar si un empleado ya fue recogido
     *
     * @param id_ruta, id de la ruta
     * @param cedula,  cedula del empleado
     * @return, TRUE si ya fue recogido, FALSE si no ha sido recogido
     */
    public boolean validarEmpleadoRecogido(String id_ruta, String cedula) {
        boolean respuesta = false;
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        respuesta = manejoDatos.validarEmpleadoRecogido(id_ruta, cedula);
        manejoDatos.close();
        return respuesta;
    }


    /**
     * Método utilizado para verificar si una ruta ya ha sido terminada
     *
     * @param id_ruta, id de la ruta
     * @return TRUE si la ruta ha sido terminada
     */
    public boolean validarRutaTerminada(String id_ruta) {
        boolean respuesta = false;
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        respuesta = manejoDatos.validarRutaTerminada(id_ruta);
        manejoDatos.close();
        return respuesta;
    }

    /**
     * Método utilizado para verificar si la ruta se puede terminar
     *
     * @param id_ruta, id de la ruta
     * @return, TRUE si se puede terminar, FALSE si no se puede terminar
     */
    public boolean validarTerminarRuta(String id_ruta) {
        boolean respuesta = false;
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        respuesta = manejoDatos.validarTerminarRuta(id_ruta);
        manejoDatos.close();
        return respuesta;
    }

    /**
     * Método utilizado para modificar las coordenadas de recogida de un usuario
     *
     * @param id_ruta,  id de la ruta
     * @param cedula,   cedula del empleado
     * @param latitud,  latitud de recogida
     * @param longitud, longitud de recogida
     */
    public void modificarCoordenadas(String id_ruta, String cedula, String latitud, String longitud) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.modificarCoordenadas(id_ruta, cedula, latitud, longitud);
        manejoDatos.close();
    }


    /**
     * Método utilizado para modificar el estado de un archivo en la base de datos
     *
     * @param id_ruta, id de la ruta
     * @param src,     Url del archivo
     */
    public void modificarCoordenadas(String id_ruta, String src) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.modificarEstadoArchivo(id_ruta, src);
        manejoDatos.close();
    }

    /**
     * Método para modificar el estado de la foto de un usuario
     *
     * @param id_ruta, id de la ruta
     * @param cedula,  cedula del usuario
     * @param foto,    estado de la foto
     */
    public void modificarFoto(String id_ruta, String cedula, int foto) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.modificarFoto(id_ruta, cedula, foto);
        manejoDatos.close();
    }

    /**
     * Método utilizado para modificar la url de la foto de un usuario
     *
     * @param id_ruta,  id de la ruta
     * @param cedula,   cedula del usuario
     * @param url_foto, url de la foto
     */
    public void modificarUrlFoto(String id_ruta, String cedula, String url_foto) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.modificarUrlFoto(id_ruta, cedula, url_foto);
        manejoDatos.close();
    }

    /**
     * Método para modificar el estado de la llamada de un usuario
     *
     * @param id_ruta, id de la ruta
     * @param cedula,  cedula del usuario
     * @param llamada, estado de la llamada
     */
    public void modificarLlamada(String id_ruta, String cedula, int llamada) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.modificarLlamada(id_ruta, cedula, llamada);
        manejoDatos.close();
    }

    /**
     * Método utilizado para terminar las opciones de un usuario
     *
     * @param id_ruta, id de la ruta
     * @param cedula,  cedula del usuario
     */
    public void terminarOpcionesUsuario(String id_ruta, String cedula) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.terminarOpcionesEmpleado(id_ruta, cedula);
        manejoDatos.close();
    }

    /**
     * Método para modificar el estado de la recogida de un usuario
     *
     * @param id_ruta,  id de la ruta
     * @param cedula,   cedula del usuario
     * @param recogida, estado de la recogida
     */
    public void modificarRecogida(String id_ruta, String cedula, int recogida) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.modificarRecogida(id_ruta, cedula, recogida);
        manejoDatos.close();
    }

    /**
     * Método utilizado para ingresar observaciones al estado de un usuario
     *
     * @param id_ruta,       id de la ruta
     * @param cedula,        cedula del usuario
     * @param observaciones, observaciones a ingresar
     */
    public void modificarObservaciones(String id_ruta, String cedula, String observaciones) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.modificarObservaciones(id_ruta, cedula, observaciones);
        manejoDatos.close();
    }

    /**
     * Método utilizado para ingresar el tiempo de espera de un usuario
     *
     * @param id_ruta, id de la ruta
     * @param cedula,  cedula del usuario
     * @param tiempo,  tiempo de espera
     */
    public void modificarTiempoEspera(String id_ruta, String cedula, String tiempo) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.modificarTiempoEspera(id_ruta, cedula, tiempo);
        manejoDatos.close();
    }

    /**
     * Método utilizado para obtener las rutas almacenadas en la base de datos
     *
     * @return, lista de rutas
     */
    public ArrayList<Ruta> obtenerTodasLasRutas() {
        ArrayList<Ruta> rutas = new ArrayList<Ruta>();
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        rutas = manejoDatos.getTodasLasRutas();
        manejoDatos.close();
        return rutas;
    }


    /**
     * Método utilizado para obtener los archivos que deben subirse al servidor
     *
     * @return, lista de archivos IMG
     */
    public ArrayList<Archivo> obtenerArchivosASubirIMG() {
        ArrayList<Archivo> archivos = new ArrayList<Archivo>();
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        archivos = manejoDatos.getArchivosIMGParaSubir();
        manejoDatos.close();
        return archivos;
    }

    /**
     * Método utilizado para obtener los archivos que deben subirse al servidor
     *
     * @return, lista de archivos JSON
     */
    public ArrayList<Archivo> obtenerArchivosASubirJSON() {
        ArrayList<Archivo> archivos = new ArrayList<Archivo>();
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        archivos = manejoDatos.getArchivosJSONParaSubir();
        manejoDatos.close();
        return archivos;
    }

    /**
     * Método utilizado para obtener la información de una ruta especifica
     *
     * @param id_ruta, id de la ruta
     * @return, información de la ruta
     */
    public Ruta getRuta(String id_ruta) {
        Ruta ruta;
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        ruta = manejoDatos.getRuta(id_ruta);
        manejoDatos.close();
        return ruta;
    }

    /**
     * Método utilizado para terminar una ruta
     *
     * @param id_ruta,  id de la ruta
     * @param latitud,  latitud
     * @param longitud, longitud
     */
    public void terminarRuta(String id_ruta, String latitud, String longitud) {
        ManejoDatosEmergia manejoDatos = new ManejoDatosEmergia(context);
        manejoDatos.open();
        manejoDatos.finalizarRuta(id_ruta, latitud, longitud);
        manejoDatos.close();
        guardar("ruta", "");
    }

    /**
     * Método utilizado para mostrar una alerta al usuario
     *
     * @param mensaje, mensaje a mostrar
     */
    public void alerta(String mensaje) {
        if (!((Activity) context).isFinishing()) {
            final AlertDialog.Builder alertaSimple = new AlertDialog.Builder(context);
            alertaSimple.setTitle("Aviso");
            alertaSimple.setMessage(mensaje);
            alertaSimple.setCancelable(false);
            alertaSimple.setPositiveButton("Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            alertaSimple.show();
        }
    }

    public JSONObject generarJSON(String id_ruta) {
        JSONObject json = new JSONObject();
        JSONObject json_ruta = new JSONObject();
        Ruta ruta = getRuta(id_ruta);
        if (ruta != null) {
            try {
                json_ruta.put("id", ruta.getId_ruta());
                json_ruta.put("nombre", ruta.getNombre());
                json_ruta.put("hora_inicio", ruta.getHora_inicio());
                json_ruta.put("hora_fin", ruta.getHora_fin());
                json_ruta.put("latitud_inicio", ruta.getLat_inicio());
                json_ruta.put("longitud_inicio", ruta.getLong_inicio());
                json_ruta.put("latitud_fin", ruta.getLat_fin());
                json_ruta.put("longitud_fin", ruta.getLong_fin());

                ArrayList<Empleado> empleados = ruta.getEmpleados();
                JSONArray array_empleados = new JSONArray();
                for (int i = 0; i < empleados.size(); i++) {
                    Empleado emp = empleados.get(i);
                    JSONObject empleado = new JSONObject();
                    empleado.put("cedula", emp.getCedula());
                    empleado.put("nombre", emp.getNombre());
                    empleado.put("direccion", emp.getDireccion());
                    empleado.put("telefono", emp.getTelefono());
                    empleado.put("recogido", emp.isRecogido());
                    empleado.put("foto", emp.isFoto());
                    empleado.put("url_foto", emp.getUrl_foto());
                    empleado.put("latitud", emp.getLatitud());
                    empleado.put("longitud", emp.getLongitud());
                    empleado.put("hora_recogido", emp.getHora());
                    empleado.put("tiempo_espera", emp.getTiempo_espera());
                    empleado.put("llamada", emp.isLlamada());
                    empleado.put("observaciones", emp.getObservaciones());
                    array_empleados.put(empleado);
                }
                json_ruta.put("empleados", array_empleados);
                json.put("ruta", json_ruta);
                return json;
            } catch (Exception e) {
            }
        }
        return json;
    }

    public int subirArchivo(String ubicacionArchivo, String url_servidor, String nombre) {


        String fileName = ubicacionArchivo;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(ubicacionArchivo);

        if (!sourceFile.isFile()) {
            //no existe
            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(url_servidor);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=" + nombre + "; filename="
                        + fileName + "" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                codigoRespuestaServidor = conn.getResponseCode();
                //String serverResponseMessage = conn.getResponseMessage();

                //mostrarToast(serverResponseMessage);

                //Log.v("Emergia "+codigoRespuestaServidor,"Emergia "+serverResponseMessage);

                if (codigoRespuestaServidor == 200) {
                    //se subio bien
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                //error
            } catch (Exception e) {
                //error
            }
            return codigoRespuestaServidor;
        }
    }

    public boolean validarEmail(String email) {
        try {
            Pattern pattern;
            Matcher matcher;
            String EXP_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            pattern = Pattern.compile(EXP_EMAIL);
            matcher = pattern.matcher(email);
            return matcher.matches();
        } catch (Exception e) {
            return true;
        }
    }
}
