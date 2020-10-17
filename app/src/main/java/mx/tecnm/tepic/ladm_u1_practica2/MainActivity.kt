package mx.tecnm.tepic.ladm_u1_practica2

import android.content.pm.PackageManager
import android.media.audiofx.EnvironmentalReverb
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Botón de guardar
        guardar.setOnClickListener {
            //En memoria interna
            if (rbinterna.isChecked) {
                if(guardarEnMemoriaInterna()==true)
                {
                    AlertDialog.Builder(this).setTitle("ATENCIÓN")
                        .setMessage("SE GUARDO DATA")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
                else
                {
                    AlertDialog.Builder(this).setTitle("ERROR")
                        .setMessage("LOS DATOS NO FUERON GUARDADOS")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
            }
            //En memoria externa
            if (rbexterna.isChecked)
            {
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                )
                {
                    ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        0 )
                }
                if(guardarEnMemoriaExterna()==true)
                {
                    AlertDialog.Builder(this).setTitle("ATENCIÓN")
                        .setMessage("SE GUARDO DATA")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
                else
                {
                    AlertDialog.Builder(this).setTitle("ERROR")
                        .setMessage("LOS DATOS NO FUERON GUARDADOS")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
            }
            frase.setText("")
            nombrear.setText("")
        }
        //Botón de abrir
        abrir.setOnClickListener {
            //En memoria interna
            if (rbinterna.isChecked) {
                if(abrirDesdeMemoriaInterna()==true)
                {
                    AlertDialog.Builder(this).setTitle("ATENCIÓN")
                        .setMessage("Buscando archivo...")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
                else
                {
                    AlertDialog.Builder(this).setTitle("ERROR")
                        .setMessage("El archivo no fue encontrado")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
            }
            //En memoria externa
            if (rbexterna.isChecked)
            {
                if(abrirDesdeMemoriaExterna()==true)
                {
                    AlertDialog.Builder(this).setTitle("ATENCIÓN")
                        .setMessage("Buscando archivo...")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
                else
                {
                    AlertDialog.Builder(this).setTitle("ERROR")
                        .setMessage("El archivo no fue encontrado")
                        .setPositiveButton("ok"){d,i->d.dismiss()}
                        .show()
                }
            }
        }
    }

    //Guardar en memoria interna
    private fun guardarEnMemoriaInterna(): Boolean
    {
        try
        {
            var archivo = nombrear.text.toString()
            var texto = frase.text.toString()
            var flujoSalida = OutputStreamWriter(openFileOutput(archivo, MODE_PRIVATE))
            flujoSalida.write(texto)
            flujoSalida.flush()
            flujoSalida.close()
        }
        catch (io: IOException)
        {
            return false
        }
        return true;
    }

    //Abrir desde memoria interna
    private fun abrirDesdeMemoriaInterna(): Boolean
    {
        if (fileList().contains(nombrear.text.toString()))
        {
            try
            {
                val archivo = InputStreamReader(openFileInput(nombrear.text.toString()))
                val br = BufferedReader(archivo)
                var linea = br.readLine()
                val todo = StringBuilder()
                while (linea != null) {
                    todo.append(linea + "\n")
                    linea = br.readLine()
                }
                br.close()
                archivo.close()
                AlertDialog.Builder(this).setTitle("DATOS ALMACENADOS:")
                    .setMessage(todo)
                    .setPositiveButton("ok") { d, i -> d.dismiss() }
                    .show()
            }
            catch (e: IOException)
            {
                AlertDialog.Builder(this).setTitle("ERROR")
                    .setMessage("ERROR, ARCHIVO NO ENCONTRADO")
                    .setPositiveButton("OK") {d,i-> d.dismiss()}
                    .show()
                return false
            }
        }
        else
        {
            return false
        }
        return true
    }

    //Guardar desde memoria externa
    private fun guardarEnMemoriaExterna() :Boolean
    {
        try
        {
            if(Environment.getExternalStorageState()!=Environment.MEDIA_MOUNTED)
            {
                AlertDialog.Builder(this).setTitle("Error")
                    .setMessage("No se detectó memoria externa")
                    .setPositiveButton("OK"){d,i->d.dismiss()}
                    .show()
                return false
            }
            var archivo = nombrear.text.toString()
            var texto = frase.text.toString()
            var rutaSD=Environment.getExternalStorageDirectory()
            var archivoSD=File(rutaSD.absolutePath,archivo)
            //Verificación de existencia del archivo
            if(!archivoSD.exists())
            {
                var flujoSalida=OutputStreamWriter(FileOutputStream(archivoSD))
                flujoSalida.write(texto)
                flujoSalida.flush()
                flujoSalida.close()
            }
            else
            {
                AlertDialog.Builder(this).setTitle("Error")
                    .setMessage("El archivo se sobreescribió")
                    .setPositiveButton("OK"){d,i->d.dismiss()}
                    .show()
                var flujoSalida=OutputStreamWriter(FileOutputStream(archivoSD))
                flujoSalida.write(texto)
                flujoSalida.flush()
                flujoSalida.close()
            }

        }
        catch (io:Exception)
        {
            AlertDialog.Builder(this).setTitle("Error")
                .setMessage("No se pudo guardar")
                .setPositiveButton("OK"){d,i->d.dismiss()}
                .show()
            return false
        }
        return true
    }

    //Abrir desde memoria externa
    private fun abrirDesdeMemoriaExterna(): Boolean
    {
        try
        {
            //Verificación de permisos
            if(Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
            {
                AlertDialog.Builder(this).setTitle("ERROR")
                    .setMessage("Error de lectura de memoria externa")
                    .setPositiveButton("OK") {d,i->d.dismiss()}
                    .show()
                return false
            }
            var archivo = nombrear.text.toString()
            var rutaSD=Environment.getExternalStorageDirectory()
            var archivoSD=File(rutaSD.absolutePath,archivo)
            if(archivoSD.exists())
            {
                val leer = InputStreamReader(FileInputStream(archivoSD))
                val br = BufferedReader(leer)
                var linea = br.readLine()
                val todo = StringBuilder()
                while (linea != null) {
                    todo.append(linea + "\n")
                    linea = br.readLine()
                }
                br.close()
                leer.close()
                AlertDialog.Builder(this).setTitle("DATOS ALMACENADOS:")
                    .setMessage(todo)
                    .setPositiveButton("ok") { d, i -> d.dismiss() }
                    .show()
            }
            else
            {
                AlertDialog.Builder(this).setTitle("ERROR")
                    .setMessage("ERROR, ARCHIVO NO ENCONTRADO")
                    .setPositiveButton("OK") {d,i-> d.dismiss()}
                    .show()
            }
        }
        catch (IO: java.lang.Exception)
        {
            AlertDialog.Builder(this).setTitle("Error")
                .setMessage("Error en el archivo")
                .setPositiveButton("OK"){d,i->d.dismiss()}
                .show()
            return false
        }
        return true
    }
}