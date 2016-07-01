package mobility.com.emergia;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import objetos.Archivo;
import objetos.Parametros;
import objetos.Ruta;
import objetos.Usuario;
import servicios.service;
import volley.CustomVolleyRequestQueue;

public class Principal extends AppCompatActivity {

    @Bind(R.id.lista_rutas_hoy)
    ListView lista_rutas;
    private Usuario usuario;
    private Context context;
    private ArrayList<HashMap<String, String>> elementosLista;
    private SimpleAdapter adapter;
    private static final String KEY_ID = "id";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_HORA = "hora";
    private static final String KEY_FECHA = "fecha";
    private ProgressDialog pd;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        inicializarObjetos();
        iniciarServicio();
        if (usuario.verificarRutaActiva()) {
            irARuta();
        } else {
            iniciarLista();
            obtenerRutas();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.historial) {
            historial();
            return true;
        } else if (id == R.id.actualizar) {
            obtenerRutas();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void iniciarServicio() {
        try {
            detenerServicio();
        } catch (Exception e) {
        }
        Intent intent = new Intent(this, service.class);
        startService(intent);
    }

    public void detenerServicio() {
        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            stopService(new Intent(this, service.class));
        } catch (Exception e) {
        }
    }

    public void irARuta() {
        Intent intent = new Intent(Principal.this, InformacionRuta.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Método utilizado para inicializar los objetos de la actividad
     */
    public void inicializarObjetos() {
        ButterKnife.bind(this);
        context = this;
        usuario = new Usuario(context);
        elementosLista = new ArrayList<HashMap<String, String>>();
        queue = CustomVolleyRequestQueue.getInstance(context)
                .getRequestQueue();
    }

    /**
     * Evento generado al presionar el botón "Terminar"
     */
    @OnClick(R.id.boton_terminar)
    public void terminar() {
        final AlertDialog.Builder confirmar = new AlertDialog.Builder(
                this);
        confirmar.setTitle("Cerrar Sesión");
        confirmar.setMessage("¿Estás seguro que deseas cerrar la sesión?");
        confirmar.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        usuario.cerrarSesion();
                        detenerServicio();
                        Intent intent = new Intent(Principal.this, Inicio.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
        confirmar.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        confirmar.show();
    }

    /**
     * Método utilizado para capturar el evento de click sobre un elemento de la lista de rutas
     *
     * @param parent,   Padre de la vista
     * @param view,     Vista sobre la que se dio click
     * @param position, Posición en la lista
     * @param id,       Id del elemento clickeado
     */
    @OnItemClick(R.id.lista_rutas_hoy)
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        if (usuario.verificarGPS()) {
            TextView nombre_ruta = (TextView) view.findViewById(R.id.nombre_ruta);
            String nombre = nombre_ruta.getText().toString();

            TextView id_ruta = (TextView) view.findViewById(R.id.id_ruta_hoy);
            String id_ruta_hoy = id_ruta.getText().toString();
            opcionesRuta(nombre, id_ruta_hoy);
        } else {
            usuario.preguntaHabilitarGPS();
        }
    }

    /**
     * Método utilizado para inicializar la lista de rutas
     */
    public void iniciarLista() {
        adapter = new SimpleAdapter(context, elementosLista,
                R.layout.item_rutas_hoy,
                new String[]{KEY_NOMBRE, KEY_ID, KEY_HORA, KEY_FECHA}, new int[]{
                R.id.nombre_ruta, R.id.id_ruta_hoy, R.id.hora_ruta_hoy, R.id.fecha_ruta_hoy});
        lista_rutas.setAdapter(adapter);
    }

    /**
     * Método utilizado para actualizar la información de la lista de rutas cuando se adiciona
     * o elimina un elemento
     */
    public void actualizarInformacionLista() {
        ((SimpleAdapter) lista_rutas.getAdapter()).notifyDataSetChanged();
    }

    /**
     * Método utilizado para obtener las rutas de hoy asociadas a una placa especifica
     */
    public void obtenerRutas() {
        iniciarProgressDialog();
        final String URL = Parametros.URL_DESCARGAR_RUTAS_HOY;

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cerrarProgressDialog();
                        try {
                            JSONObject json = new JSONObject(response);
                            boolean estado = Boolean.valueOf(json.getString("state"));
                            if (estado) {
                                JSONArray rutas = json.getJSONArray("routes");
                                adicionarRutas(rutas);
                            } else {
                                usuario.mostrarToast("No hay rutas creadas para el vehiculo " + usuario.obtener("placa"));
                            }
                        } catch (Exception e) {
                            usuario.mostrarToast("Hubo un error obteniendo la información, por favor intenta de nuevo!");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        cerrarProgressDialog();
                        usuario.mostrarToast(volleyError.toString() + " Hubo un error obteniendo la información, por favor intenta de nuevo!");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("plaque", usuario.obtener("placa"));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    /**
     * Método utilizado para mostrar una barra de progreso en la interfaz
     */
    public void iniciarProgressDialog() {
        pd = ProgressDialog.show(context, "Un segundo", "Realizando la petición", true, false);
    }

    /**
     * Método utilizado para cerrar la barra de progreso abierta en la interfaz
     */
    public void cerrarProgressDialog() {
        try {
            pd.dismiss();
        } catch (Exception e) {
        }
    }

    /**
     * Método utilizado para mostrar las rutas en la interfaz
     *
     * @param rutas, Rutas obtenidas del servidor
     */
    public void adicionarRutas(JSONArray rutas) throws JSONException {
        HashMap<String, String> map;
        for (int i = 0; i < rutas.length(); i++) {
            JSONObject json = rutas.getJSONObject(i);
            map = new HashMap<String, String>();
            map.put(KEY_ID, json.getString("id"));
            map.put(KEY_NOMBRE, json.getString("name"));
            map.put(KEY_HORA, json.getString("start_time") + " - " + json.getString("end_time"));
            map.put(KEY_FECHA, "Fecha: " + json.getString("date"));
            if (!usuario.validarRutaTerminada(json.getString("id"))) {
                if (!elementosLista.contains(map)) {
                    elementosLista.add(map);
                }
            } else {
                //usuario.mostrarToast("la ruta "+json.getString("id")+" ya se termino");
                //Ya se termino la ruta
            }
        }
        actualizarInformacionLista();
    }


    /**
     * Método utilizado para mostrar las opciones de cada ruta
     *
     * @param nombre_ruta, Nombre de la ruta a iniciar
     * @param id_ruta,     Id de la ruta a iniciar
     */
    public void opcionesRuta(final String nombre_ruta, final String id_ruta) {
        final String[] items = {"Iniciar ruta", "Ver ruta (Observar personas de la ruta)", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("¿Opciones de la ruta '" + nombre_ruta + "'?");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Iniciar ruta")) {
                    //PREGUNTAMOS SI LA QUIERE INICIAR
                    usuario.iniciarRuta(id_ruta, nombre_ruta);
                    irARuta();
                } else if (items[item].equals("Ver ruta (Observar personas de la ruta)")) {
                    usuario.guardar("ruta_ver", id_ruta);
                    Intent intent = new Intent(Principal.this, VerEmpleados.class);
                    startActivity(intent);
                }
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void historial() {
        Intent intent = new Intent(Principal.this, Historial.class);
        startActivity(intent);
    }

}
