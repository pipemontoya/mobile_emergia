package mobility.com.emergia;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Registro extends AppCompatActivity {

    @Bind(R.id.cedula)
    EditText cedula;
    @Bind(R.id.nombres)
    EditText nombres;
    @Bind(R.id.apellidos)
    EditText apellidos;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.telefono)
    EditText telefono;
    private Usuario usuario;
    private Context context;
    private ProgressDialog pd;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        inicializarObjetos();
    }

    public void inicializarObjetos() {
        ButterKnife.bind(this);
        context = this;
        usuario = new Usuario(context);
        queue = CustomVolleyRequestQueue.getInstance(context)
                .getRequestQueue();
    }

    @OnClick(R.id.registro)
    public void registro() {
        String cedula_usuario = usuario.obtenerTextoEditText(cedula);
        String nombres_usuario = usuario.obtenerTextoEditText(nombres);
        String apellidos_usuario = usuario.obtenerTextoEditText(apellidos);
        String email_usuario = usuario.obtenerTextoEditText(email).trim();
        String telefono_usuario = usuario.obtenerTextoEditText(telefono);
        if (usuario.validarCampo(cedula_usuario) &&
                usuario.validarCampo(nombres_usuario) &&
                usuario.validarCampo(apellidos_usuario) &&
                usuario.validarCampo(email_usuario) &&
                usuario.validarCampo(telefono_usuario)) {
            if (usuario.validarEmail(email_usuario)) {
                registro(cedula_usuario, nombres_usuario, apellidos_usuario, email_usuario, telefono_usuario);
            } else {
                usuario.mostrarToast("Por favor ingresa un email valido!!");
            }
        } else {
            usuario.mostrarToast("Todos los campos son obligatorios, por favor verifica la información");
        }
    }


    public void registro(final String cedula, final String nombres, final String apellidos, final String email, final String telefono) {
        iniciarProgressDialog();
        final String URL = Parametros.URL_REGISTRO;

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        cerrarProgressDialog();
                        try {
                            JSONObject json = new JSONObject(response);
                            boolean estado = Boolean.valueOf(json.getString("state"));
                            if (estado) {
                                usuario.alerta("El registro se realizo correctamente!!");
                            } else {
                                usuario.alerta("No se pudo realizar el registro!! "+json.toString());
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
                params.put("name", nombres);
                params.put("lastname", apellidos);
                params.put("phone", telefono);
                params.put("email", email);
                params.put("active", "1");
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
}
