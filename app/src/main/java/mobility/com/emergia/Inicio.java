package mobility.com.emergia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import objetos.Parametros;
import objetos.Usuario;
import volley.CustomVolleyRequestQueue;

public class Inicio extends AppCompatActivity {

    @Bind(R.id.cedula)
    EditText cedula;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private Usuario usuario;
    private Context context;
    private ProgressDialog pd;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        inicializarObjetos();
        setSupportActionBar(toolbar);
        toolbar.setTitle("Transmilenium");
        if(usuario.verificarSesion()){
            irAPrincipal();
        }
    }


    /**
     * Método utilizado para inicializar los objetos de la actividad
     */
    public void inicializarObjetos() {
        ButterKnife.bind(this);
        context = this;
        usuario = new Usuario(context);
        queue = CustomVolleyRequestQueue.getInstance(context)
                .getRequestQueue();
    }

    /**
     * Método que se llama cuando el usuario hace click sobre el botón flotante de opciones
     *
     * @param vista, Vista actual
     */
    @OnClick(R.id.opciones)
    public void irAregistro(View vista) {
        Snackbar.make(vista, "", Snackbar.LENGTH_LONG)
                .setAction("Registrarse", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Inicio.this, Registro.class);
                        startActivity(intent);
                    }
                }).show();
    }


    /**
     * Metodo que se llama cuando el usuario hace click sobre el botón iniciar
     *
     * @param vista, Vista actual
     */
    public void iniciar(View vista) {
        String cedula_ingresada = usuario.obtenerTextoEditText(cedula);
        if (usuario.validarCampo(cedula_ingresada)) {
            if (usuario.verificaConexion()) {
                iniciarSesion(cedula_ingresada);
            } else {
                usuario.mostrarToast("Por favor revisa tu conexión a internet");
            }
        } else {
            usuario.mostrarToast("Por favor ingresa una cédula valida");
        }
    }

    /**
     * Método utilizado para realizar el login en el servidor
     *
     * @param cedula, Cédula del usuario
     */
    public void iniciarSesion(final String cedula) {
        iniciarProgressDialog();
        final String URL = Parametros.URL_LOGIN;

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cerrarProgressDialog();
                        try {
                            JSONObject json = new JSONObject(response);
                            boolean estado = Boolean.valueOf(json.getString("state"));
                            if (estado) {
                                JSONObject conductor = json.getJSONObject("driver");
                                usuario.guardarSesion(conductor.getString("dni"),
                                        conductor.getString("vehicles_plaque"),
                                        conductor.getString("name") + " " + conductor.getString("lastname"));
                                irAPrincipal();
                                //usuario.mostrarToast(conductor.getString("dni")+", "+conductor.getString("vehicles_plaque")+", "+conductor.getString("name") + " " + conductor.getString("lastname"));
                            } else {
                                usuario.mostrarToast("Los datos son incorrectos, por favor verifica la información!");
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
                        usuario.mostrarToast("Hubo un error obteniendo la información, por favor intenta de nuevo!");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("dni", cedula);
                params.put("password", usuario.getDeviceId());
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
     * Método utilizado para lanzar la actividad Registro
     */
    public void irAPrincipal(){
        Intent intent = new Intent(Inicio.this, Principal.class);
        startActivity(intent);
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
}
