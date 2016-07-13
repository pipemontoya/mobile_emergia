package mobility.com.emergia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import objetos.Parametros;
import objetos.Usuario;
import volley.CustomVolleyRequestQueue;

public class VerEmpleados extends AppCompatActivity {

    @Bind(R.id.lista_ver_empleados)
    ListView lista_empleados;
    @Bind(R.id.buscar)
    EditText buscar;
    private Usuario usuario;
    private Context context;
    private ArrayList<HashMap<String, String>> elementosLista;
    private SimpleAdapter adapter;
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_DIR = "direccion";
    private static final String KEY_CED = "cedula";
    private static final String KEY_TEL = "telefono";
    private ProgressDialog pd;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_empleados);
        inicializarObjetos();
        iniciarLista();
        escucharEventos();
        obtenerEmpleados();
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
     * Método utilizado para escuchar los eventos en la interfaz de usuario
     */
    public void escucharEventos() {
        buscar.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                VerEmpleados.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    /**
     * Método utilizado para inicializar la lista de empleados
     */
    public void iniciarLista() {
        adapter = new SimpleAdapter(context, elementosLista,
                R.layout.item_empleado,
                new String[]{KEY_NOMBRE, KEY_DIR, KEY_CED, KEY_TEL}, new int[]{
                R.id.nombre_persona, R.id.direccion_persona, R.id.cedula_persona, R.id.telefono_persona});
        lista_empleados.setAdapter(adapter);
    }


    /**
     * Método utilizado para actualizar la información de la lista de los empleados cuando se adiciona
     * o elimina un elemento
     */
    public void actualizarInformacionLista() {
        ((SimpleAdapter) lista_empleados.getAdapter()).notifyDataSetChanged();
    }

    /**
     * Método utilizado para capturar el evento de click sobre un elemento de la lista de empleados
     *
     * @param parent,   Padre de la vista
     * @param view,     Vista sobre la que se dio click
     * @param position, Posición en la lista
     * @param id,       Id del elemento clickeado
     */
    @OnItemClick(R.id.lista_ver_empleados)
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        if (usuario.verificarGPS()) {
            TextView nombre_persona = (TextView) view.findViewById(R.id.nombre_persona);
            String nombre = nombre_persona.getText().toString();
            TextView cedula_persona = (TextView) view.findViewById(R.id.cedula_persona);
            String cedula = cedula_persona.getText().toString();
            TextView telefono_persona = (TextView) view.findViewById(R.id.telefono_persona);
            String telefono = telefono_persona.getText().toString();
            opcionesPersona(nombre, cedula, telefono);
        } else {
            usuario.preguntaHabilitarGPS();
        }
    }

    /**
     * Método que se llama cuando el usuario presiona el botón volver
     */
    @OnClick(R.id.boton_volver)
    public void volver() {
        Intent intent = new Intent(VerEmpleados.this, Principal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    /**
     * Método utilizado para obtener los empleados asociados a una ruta especifica
     */
    public void obtenerEmpleados() {
        iniciarProgressDialog();
        final String URL = Parametros.URL_DESCARGAR_EMPLEADOS_RUTA;

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cerrarProgressDialog();
                        try {
                            JSONObject json = new JSONObject(response);
                            boolean estado = Boolean.valueOf(json.getString("state"));
                            if (estado) {
                                JSONArray empleados = json.getJSONArray("employees");
                                adicionarEmpleados(empleados);
                            } else {
                                usuario.mostrarToast("No hay empleados asociados a la ruta");
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
                params.put("id", usuario.obtener("ruta_ver"));
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


    public void adicionarEmpleados(JSONArray array_empleados) throws JSONException {
        HashMap<String, String> map;
        for (int i = 0; i < array_empleados.length(); i++) {
            JSONObject aux_empleado = array_empleados.getJSONObject(i);
            map = new HashMap<String, String>();
            map.put(KEY_NOMBRE, aux_empleado.getString("name") + " " + aux_empleado.getString("lastname"));
            map.put(KEY_DIR, aux_empleado.getString("address") + " " + aux_empleado.getString("neighborhood"));
            map.put(KEY_CED, aux_empleado.getString("dni"));
            map.put(KEY_TEL, aux_empleado.getString("mobile"));
            elementosLista.add(map);
        }
        actualizarInformacionLista();
    }

    public void opcionesPersona(final String persona, final String cedula, final String telefono) {
        final String[] items = {"Llamar", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("¿Deseas llamar a " + persona + "?");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Llamar")) {
                    usuario.llamar(telefono);
                }
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
