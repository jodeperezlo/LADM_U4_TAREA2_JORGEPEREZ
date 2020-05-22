package mx.edu.ittepic.ladm_u4_tarea2_contentproviders

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CallLog
import android.provider.Telephony
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var REQUEST_PERMISOS = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Solicitar los permisos, en caso de no haber sido otorgados.
        solicitarPermisos()

        btnBorrar.setOnClickListener {
            textView.setText("NO HA CONSULTADO NADA")
        }

        btnVerLlamadas.setOnClickListener {
            cargarListaLlamadas()
        }

        btnVerMensajes.setOnClickListener{
            cargarListaMensajes()
        }
    }

    private fun solicitarPermisos() {
        var permisoReadCall = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CALL_LOG)
        var permisoMensajes = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)

        if(permisoReadCall != PackageManager.PERMISSION_GRANTED || permisoMensajes != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_CALL_LOG,android.Manifest.permission.READ_SMS),REQUEST_PERMISOS)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_PERMISOS){
            setTitle("PERMISOS OTORGADOS")
        }
    }

    @SuppressLint("MissingPermission")
    private fun cargarListaLlamadas() {
        var resultado = "HISTORIAL DE LLAMADAS:\n\n"

        val cursorLlamadas = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, null
        )
        if(cursorLlamadas!!.moveToFirst()){
            var numero = cursorLlamadas.getColumnIndex(CallLog.Calls.NUMBER)
            var nombre = cursorLlamadas.getColumnIndex(CallLog.Calls.CACHED_NAME)
            do{
                resultado += "Número: "+cursorLlamadas!!.getString(numero)+"\nNombre: "+cursorLlamadas.getString(nombre)+
                        "\n--------------------\n\n"
            }while (cursorLlamadas.moveToNext())
        }else{
            resultado = "LLAMADAS:\nNO HAY LLAMADAS REGISTRADAS"
        }
        textView.setText(resultado)
    }

    private fun cargarListaMensajes() {
        var cursor = contentResolver.query(
            Uri.parse("content://sms/sent"),
            null, null, null, null
        )

        var resultado2 = "MENSAJES ENVIADOS:\n\n"
        if(cursor!!.moveToFirst()){
            var numero = cursor.getColumnIndex("address")
            var contenido = cursor.getColumnIndex("body")

            do{
                resultado2 += "DESTINO: " + cursor.getString(numero) +
                        "\nMENSAJE: " + cursor.getString(contenido) +
                        "\n---------------\n\n"
            }while (cursor.moveToNext())
        }else{
            resultado2 = "NO HAY SMS ENVIADOS EN BANDEJA DE MENSAJES"
        }
        textView.setText(resultado2)
    }

}
