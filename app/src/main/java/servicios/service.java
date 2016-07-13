package servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import mobility.com.emergia.InformacionRuta;
import mobility.com.emergia.Principal;
import mobility.com.emergia.R;
import objetos.Usuario;

public class service extends Service implements OnInitListener {

    private Usuario usuario;
    private LocationManager locManager;
    private LocationListener locListener;
    private String latitud;
    private String longitud;
    private Context context;
    private TextToSpeech talker;

    public service() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        inicializarObjetos();
        comenzarLocalizacion();
    }

    public void inicializarObjetos() {
        latitud = "0";
        longitud = "0";
        context = this;
        talker = new TextToSpeech(this, this);
        usuario = new Usuario(context);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        stopForeground(true);
    }

    public void leer(final String texto) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    talker.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
                } catch (Exception e) {
                }
            }
        }, 2000);
    }

    private void comenzarLocalizacion() {
        locManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        Location loc;
        loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mostrarPosicion(loc);
        //notificacionGPSOK();
        locListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mostrarPosicion(location);
            }

            public void onProviderDisabled(String provider) {
                //notificacionNOGPS();
                leer("Por favor enciende el GPS de tu dispositivo.");
            }

            public void onProviderEnabled(String provider) {
                //notificacionGPSOK();
                leer("El GPS ha sido encendido correctamente, Gracias.");
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }
        };
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                locListener);
    }

    private void mostrarPosicion(Location loc) {
        if (loc != null) {
            this.latitud = "" + loc.getLatitude();
            this.longitud = "" + loc.getLongitude();
            usuario.guardar("latitud", latitud);
            usuario.guardar("longitud", longitud);
        }
    }

    @Override
    public void onInit(int status) {
    }
}
