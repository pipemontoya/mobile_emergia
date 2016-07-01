package mobility.com.emergia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import objetos.Archivo;
import objetos.Parametros;
import objetos.Usuario;
import volley.CustomVolleyRequestQueue;

public class InformacionRuta extends AppCompatActivity {

    @Bind(R.id.lista_empleados)
    ListView lista_empleados;
    @Bind(R.id.buscar)
    EditText buscar;
    @Bind(R.id.cronometro)
    Chronometer cronometro;
    @Bind(R.id.alerta_inferior)
    LinearLayout alerta;
    @Bind(R.id.boton_terminar)
    Button terminar;
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
    private int seleccion = 0;
    private ArrayList<JSONObject> empleados;
    private boolean cronometro_iniciado = false;
    public static InformacionRuta clase_principal;
    private int respuesta;
    private String id_ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_ruta);
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
        clase_principal = this;
        usuario = new Usuario(context);
        elementosLista = new ArrayList<HashMap<String, String>>();
        empleados = new ArrayList<JSONObject>();
        queue = CustomVolleyRequestQueue.getInstance(context)
                .getRequestQueue();
        id_ruta=usuario.obtener("ruta");
    }

    /**
     * Método utilizado para escuchar los eventos en la interfaz de usuario
     */
    public void escucharEventos() {
        ocultarAlertaInferior();
        buscar.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                InformacionRuta.this.adapter.getFilter().filter(cs);
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
    @OnItemClick(R.id.lista_empleados)
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
        TextView nombre_persona = (TextView) view.findViewById(R.id.nombre_persona);
        String nombre = nombre_persona.getText().toString();
        TextView cedula_persona = (TextView) view.findViewById(R.id.cedula_persona);
        String cedula = cedula_persona.getText().toString();
        TextView telefono_persona = (TextView) view.findViewById(R.id.telefono_persona);
        String telefono = telefono_persona.getText().toString();
        mostrarOpcionesUsuario(nombre, cedula, telefono);
    }

    /**
     * Método que se llama cuando el usuario presiona el botón volver
     */
    @OnClick(R.id.boton_terminar)
    public void terminar() {
        if (usuario.validarTerminarRuta(id_ruta)) {
            final AlertDialog.Builder confirmar = new AlertDialog.Builder(
                    this);
            confirmar.setTitle("Terminar Ruta");
            confirmar.setMessage("¿Estás seguro que deseas terminar la ruta?");
            confirmar.setPositiveButton("Si",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            usuario.terminarRuta(id_ruta, usuario.obtener("latitud"), usuario.obtener("longitud"));
                            JSONObject json = usuario.generarJSON(id_ruta);
                            String nombre_json = id_ruta + "_" + usuario.getFechaActual() + ".json";
                            usuario.guardar(nombre_json, json.toString());
                            String carpeta = Environment.getExternalStorageDirectory() + File.separator + "emergia/";
                            usuario.insertarArchivo(id_ruta, carpeta + "" + nombre_json, "JSON");
                            usuario.mostrarToast("La ruta ha terminado correctamente!!");
                            //
                            subirArchivos();
                            //

                            Intent intent = new Intent(InformacionRuta.this, Principal.class);
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
        } else {
            usuario.alerta("Debes recoger a todos los usuarios para terminar la ruta!!");
        }
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
                params.put("id", id_ruta);
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
            if (!usuario.validarEmpleadoRecogido(id_ruta, aux_empleado.getString("dni"))) {
                map = new HashMap<String, String>();
                map.put(KEY_NOMBRE, aux_empleado.getString("name") + " " + aux_empleado.getString("lastname"));
                map.put(KEY_DIR, aux_empleado.getString("address") + " " + aux_empleado.getString("neighborhood"));
                map.put(KEY_CED, aux_empleado.getString("dni"));
                map.put(KEY_TEL, aux_empleado.getString("mobile"));
                usuario.adicionarEmpleadoRuta(id_ruta,
                        aux_empleado.getString("dni"),
                        aux_empleado.getString("name") + " " + aux_empleado.getString("lastname"),
                        aux_empleado.getString("mobile"),
                        aux_empleado.getString("address") + " " + aux_empleado.getString("neighborhood"));
                elementosLista.add(map);
                empleados.add(aux_empleado);
            } else {
                //usuario.mostrarToast("el usuario " + aux_empleado.getString("dni") + " ya ha sido recogido");
            }
        }
        actualizarInformacionLista();
    }

    public void mostrarAlertaInferior() {
        alerta.setVisibility(LinearLayout.VISIBLE);
        terminar.setVisibility(Button.GONE);
    }

    public void ocultarAlertaInferior() {
        alerta.setVisibility(LinearLayout.GONE);
        terminar.setVisibility(Button.VISIBLE);
    }

    public void mostrarOpcionesUsuario(final String persona, final String cedula, final String telefono) {
        mostrarAlertaInferior();
        cronometro_iniciado = false;
        cronometro.setBase(SystemClock.elapsedRealtime());
        TextView titulo = (TextView) findViewById(R.id.titulo_alerta);
        titulo.setText("¿" + persona + " se encuentra en el vehiculo?");

        final ListView lista = (ListView) findViewById(R.id.lista_alerta);

        String[] motivos = {"Si, ya se encuentra en el vehiculo",
                "Si, ya se encuentra en el vehiculo, deseo tomar una foto",
                "No se encuentra en el lugar, deseo tomar la foto para la evidencia",
                "Ya no trabaja en Emergia",
                "Llamar",
                "El asesor no contesta",
                "Recogí al usuario, pero la dirección es incorrecta",
                "No pude recoger al usuario, la dirección es incorrecta",
                "Cancelar"};
        lista.setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_single_choice,
                android.R.id.text1, motivos));
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lista.setItemChecked(8, true);
        seleccion = 8;
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                seleccion = position;
            }
        });

        Button aceptar = (Button) findViewById(R.id.boton_positivo_alerta);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (seleccion) {
                    case 0:
                        usuarioRecogido(cedula);
                        ocultarAlertaInferior();
                        break;
                    case 1:
                        irAFoto(cedula, persona, "SI");
                        ocultarAlertaInferior();
                        break;
                    case 2:
                        irAFoto(cedula, persona, "NO");
                        ocultarAlertaInferior();
                        break;
                    case 3:
                        yaNoTrabaja(cedula);
                        ocultarAlertaInferior();
                        break;
                    case 4:
                        usuario.llamar(telefono);
                        usuario.modificarLlamada(id_ruta, cedula, 1);
                        break;
                    case 5:
                        usuario.modificarObservaciones(id_ruta, cedula, "El asesor no contesta");
                        usuario.mostrarToast("Hemos registrado la observación");
                        break;
                    case 6:
                        usuarioRecogido(cedula);
                        usuario.modificarObservaciones(id_ruta, cedula, "Recogí al usuario, pero la dirección es incorrecta");
                        ocultarAlertaInferior();
                        break;
                    case 7:
                        usuarioNoRecogido(cedula);
                        usuario.modificarObservaciones(id_ruta, cedula, "No pude recoger al usuario, la dirección es incorrecta");
                        ocultarAlertaInferior();
                        break;

                    default:
                        ocultarAlertaInferior();
                        break;
                }

            }
        });

        Button volver = (Button) findViewById(R.id.boton_negativo_alerta);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cronometro.stop();
                ocultarAlertaInferior();
            }
        });

        final ImageButton iniciar_cronometro = (ImageButton) findViewById(R.id.iniciar_cronometro);
        iniciar_cronometro.setEnabled(true);
        iniciar_cronometro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciar_cronometro.setEnabled(false);
                cronometro.setBase(SystemClock.elapsedRealtime());
                cronometro.start();
                cronometro_iniciado = true;
            }
        });
    }

    public void usuarioRecogido(String cedula) {
        usuario.modificarRecogida(id_ruta, cedula, 1);
        eliminarEmpleado(cedula);
        usuario.mostrarToast("Hemos registrado la posición en la que has recogido al usuario");
    }

    public void usuarioNoRecogido(String cedula) {
        usuario.modificarRecogida(id_ruta, cedula, 0);
        eliminarEmpleado(cedula);
    }

    public void modificarLlamada(String cedula, int llamada) {
        usuario.modificarLlamada(id_ruta, cedula, llamada);
    }

    public void cambiarTiempoEspera(long espera, String cedula) {
        int segundos = (int) (espera / 1000);
        usuario.modificarTiempoEspera(id_ruta, cedula, "" + segundos);
    }

    public void eliminarEmpleado(String cedula) {
        if (cronometro_iniciado) {
            cronometro.stop();
            long tiempo = SystemClock.elapsedRealtime() - cronometro.getBase();
            cambiarTiempoEspera(tiempo, cedula);
        }
        usuario.modificarCoordenadas(id_ruta, cedula, usuario.obtener("latitud"), usuario.obtener("longitud"));
        usuario.terminarOpcionesUsuario(id_ruta, cedula);
        if (empleados.size() > 0) {
            try {
                for (int i = 0; i < empleados.size(); i++) {
                    JSONObject empleado = empleados.get(i);
                    if (empleado.getString("dni").equals(cedula)) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(KEY_NOMBRE, empleado.getString("name") + " " + empleado.getString("lastname"));
                        map.put(KEY_DIR, empleado.getString("address") + " " + empleado.getString("neighborhood"));
                        map.put(KEY_CED, empleado.getString("dni"));
                        map.put(KEY_TEL, empleado.getString("mobile"));
                        //Eliminamos el empleado de la lista y del listView
                        empleados.remove(i);
                        elementosLista.remove(map);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                actualizarInformacionLista();
                            }
                        });
                    }
                }

            } catch (Exception e) {
            }
        }
    }


    public void irAFoto(String cedula, String persona, String recogido) {
        String[] datos = new String[3];
        datos[0] = cedula;
        datos[1] = persona;
        datos[2] = recogido;
        Intent intent = new Intent(this, Foto.class);
        intent.putExtra("persona", datos);
        startActivity(intent);
    }


    public void yaNoTrabaja(String cedula) {
        usuario.modificarObservaciones(id_ruta, cedula, "Ya no trabaja en Emergia");
        usuarioNoRecogido(cedula);
        eliminarEmpleado(cedula);
    }


    public void subirArchivos() {
        ArrayList<Archivo> json_upload = usuario.obtenerArchivosASubirJSON();
        ArrayList<Archivo> img_upload = usuario.obtenerArchivosASubirIMG();

        //Subimos los archivos JSON
        for (int i = 0; i < json_upload.size(); i++) {
            final Archivo aux = json_upload.get(i);
            new Thread(new Runnable() {
                public void run() {
                    respuesta = usuario.subirArchivo(aux.getSrc(), Parametros.URL_UPLOAD_JSON, "data");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (respuesta == 200) {
                                File file = new File(aux.getSrc());
                                file.delete();
                            }
                            usuario.mostrarToast(aux.getSrc() + " -> " + respuesta);
                        }
                    });
                }
            }).start();
        }


        //Subimos los archivos IMG
        for (int i = 0; i < img_upload.size(); i++) {
            final Archivo aux = img_upload.get(i);
            new Thread(new Runnable() {
                public void run() {
                    respuesta = usuario.subirArchivo(aux.getSrc(), Parametros.URL_UPLOAD_FOTO, "photo");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (respuesta == 200) {
                                File file = new File(aux.getSrc());
                                file.delete();
                            }
                            usuario.mostrarToast(aux.getSrc() + " -> " + respuesta);
                        }
                    });
                }
            }).start();
        }


    }
}
