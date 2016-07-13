package mobility.com.emergia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import objetos.Usuario;

public class Foto extends AppCompatActivity {

    private static final int CAMERA_PIC_REQUEST = 0;
    private Context context;
    private Usuario usuario;
    private String cedula, nombre, id_ruta, encontrado;
    @Bind(R.id.cedula_foto)
    TextView cedula_usuario;
    @Bind(R.id.nombre_foto)
    TextView nombre_usuario;
    private ImageView imagenEvidencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);
        inicializarObjetos();
        mostrarCampos();
    }

    public void inicializarObjetos() {
        ButterKnife.bind(this);
        context = this;
        usuario = new Usuario(context);
        id_ruta = usuario.obtener("ruta");
        imagenEvidencia=(ImageView)findViewById(R.id.foto_asesor);
    }

    public void mostrarCampos() {
        Intent intent = getIntent();
        String[] datos = intent.getStringArrayExtra("persona");
        cedula = datos[0];
        nombre = datos[1];
        encontrado = datos[2];
        cedula_usuario.setText(cedula);
        nombre_usuario.setText(nombre);
    }

    @OnClick(R.id.foto)
    public void foto() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }

    @OnClick(R.id.volver)
    public void volver() {
        finish();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAMERA_PIC_REQUEST) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imagenEvidencia.setImageBitmap(thumbnail);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                String nombre_imagen = getNombreImagen();
                String url = Environment.getExternalStorageDirectory() + File.separator + "emergia/" + nombre_imagen;
                File file = new File(url);
                try {
                    file.createNewFile();
                    FileOutputStream fo = new FileOutputStream(file);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    //CREAR TABLA PARA LOS ARCHIVOS CON ESTADO
                    usuario.insertarArchivo(id_ruta, url, "IMG");
                    if (encontrado.equals("NO")) {
                        usuario.modificarRecogida(id_ruta, cedula, 0);
                        //usuario.usuarioNoEncontradoArchivo(context, id_ruta, cedula, usuario.obtener(context, "latitud"), usuario.obtener(context, "longitud"), nombre_imagen);
                    } else {
                        usuario.modificarRecogida(id_ruta, cedula, 1);
                        //usuario.recogerUsuarioArchivoConFoto(context, id_ruta, cedula, usuario.obtener(context, "latitud"), usuario.obtener(context, "longitud"), nombre_imagen);
                    }
                    usuario.modificarFoto(id_ruta, cedula, 1);
                    usuario.modificarUrlFoto(id_ruta, cedula, nombre_imagen);
                    usuario.modificarCoordenadas(id_ruta, cedula, usuario.obtener("latitud"), usuario.obtener("longitud"));
                    usuario.terminarOpcionesUsuario(id_ruta, cedula);
                    usuario.mostrarToast("La foto se almaceno correctamente");
                    eliminarDeLaLista(cedula);
                } catch (IOException e) {
                    usuario.mostrarToast("Hubo un error almacenando la foto, por favor toma la foto de nuevo");
                }
            }
        } catch (Exception e) {
        }
    }

    public String getNombreImagen() {
        return id_ruta + "_" + this.cedula + "_" + usuario.getFechaActual() + ".jpg";
    }

    public void eliminarDeLaLista(String cedula) {
        InformacionRuta principal = InformacionRuta.clase_principal;
        try {
            principal.eliminarEmpleado(cedula);
        } catch (Exception e) {
        }
    }
}

