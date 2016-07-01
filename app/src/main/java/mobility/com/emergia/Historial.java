package mobility.com.emergia;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import objetos.Parametros;
import objetos.Usuario;
import volley.CustomVolleyRequestQueue;

public class Historial extends AppCompatActivity {

    @Bind(R.id.lista_historial_rutas)
    ListView lista_rutas;
    private Usuario usuario;
    private Context context;
    private ArrayList<HashMap<String, String>> elementosLista;
    private SimpleAdapter adapter;
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_HORA_FIN = "hora_fin";
    private static final String KEY_HORA_INICIO = "hora_inicio";
    private static final String KEY_FECHA = "fecha";
    private ProgressDialog pd;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);
        inicializarObjetos();
        iniciarLista();
        obtenerRutas();
    }

    public void inicializarObjetos() {
        ButterKnife.bind(this);
        context = this;
        usuario = new Usuario(context);
        elementosLista = new ArrayList<HashMap<String, String>>();
        queue = CustomVolleyRequestQueue.getInstance(context)
                .getRequestQueue();
    }

    public void iniciarLista() {
        adapter = new SimpleAdapter(context, elementosLista,
                R.layout.item_historial,
                new String[]{KEY_NOMBRE, KEY_FECHA, KEY_HORA_INICIO, KEY_HORA_FIN}, new int[]{
                R.id.nombre_ruta, R.id.fecha_ruta, R.id.hora_inicio, R.id.hora_fin});
        lista_rutas.setAdapter(adapter);
    }

    public void actualizarInformacionLista() {
        ((SimpleAdapter) lista_rutas.getAdapter()).notifyDataSetChanged();
    }


    public void obtenerRutas() {
        iniciarProgressDialog();
        final String URL = Parametros.URL_OBTENER_HISTORIAL_RUTAS;

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
                                sinHistorial();
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
            map.put(KEY_NOMBRE, json.getString("name"));
            map.put(KEY_FECHA, json.getString("date"));
            map.put(KEY_HORA_INICIO, "Inicio: " + json.getString("start_time"));
            map.put(KEY_HORA_FIN, "Fin: " + json.getString("end_time"));
            if (!elementosLista.contains(map)) {
                elementosLista.add(map);
            }
        }
        actualizarInformacionLista();
    }

    public void sinHistorial() {
        HashMap<String, String> map;
        map = new HashMap<String, String>();
        map.put(KEY_NOMBRE, "No tienes rutas terminadas!!");
        map.put(KEY_FECHA, "");
        map.put(KEY_HORA_INICIO, "");
        map.put(KEY_HORA_FIN, "");
        elementosLista.add(map);
        actualizarInformacionLista();
    }
}
