package com.example.mapclientapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.io.ByteArrayInputStream
import android.util.Base64

class MainActivity : AppCompatActivity() {

//    private val NAMESPACE = "http://example.com/stm45"
    private val URL = "http://localhost:8090/MapService?wsdl"
    private val METHOD_NAME = "getFullMap"
    private val SOAP_ACTION = "http://example.com/stm45/getFullMap"
    private val NAMESPACE = "http://example.com/stm45"

    private lateinit var left_X1: EditText
    private lateinit var left_Y1: EditText
    private lateinit var right_X2: EditText
    private lateinit var right_Y2: EditText
    private lateinit var imgMap: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //widoki
        left_X1 = findViewById(R.id.left_X1)
        left_Y1 = findViewById(R.id.left_Y1)
        right_X2 = findViewById(R.id.right_X2)
        right_Y2 = findViewById(R.id.right_Y2)
        imgMap = findViewById(R.id.imgMap)

        //button onclick wywolanie api
        val btnFetchMap: Button = findViewById(R.id.btnFetchMap)
        btnFetchMap.setOnClickListener { fetchMapFragment() }
    }

    private fun fetchMapFragment() {
        val x1Str = left_X1.text.toString()
        val y1Str = left_Y1.text.toString()
        val x2Str = right_X2.text.toString()
        val y2Str = right_Y2.text.toString()

        //walidacja
        if (x1Str.isEmpty() || y1Str.isEmpty() || x2Str.isEmpty() || y2Str.isEmpty()) {
            Toast.makeText(this, "Wprowadź wszystkie współrzędne!", Toast.LENGTH_SHORT).show()
            return
        }

        val x1 = x1Str.toInt()
        val y1 = y1Str.toInt()
        val x2 = x2Str.toInt()
        val y2 = y2Str.toInt()

        if (x1 < 0 || x1 > 1000 || y1 < 0 || y1 > 1000 || x2 < 0 || x2 > 1000 || y2 < 0 || y2 > 1000) {
            Toast.makeText(this, "Współrzędne muszą być w zakresie od 0 do 1000!", Toast.LENGTH_SHORT).show()
            return
        }

        if (x1 >= x2 || y1 >= y2) {
            Toast.makeText(this, "Lewy górny róg musi być mniejszy od prawego dolnego rogu!", Toast.LENGTH_SHORT).show()
            return
        }

        //pobieranie fragmentu zdjecia mapy
        Thread {
            try {
                //soap
                val request = SoapObject(NAMESPACE, METHOD_NAME)

                //dodanie parametrow
                addProperty(request, "topLeftX", x1, Int::class.java)
                addProperty(request, "topLeftY", y1, Int::class.java)
                addProperty(request, "bottomRightX", x2, Int::class.java)
                addProperty(request, "bottomRightY", y2, Int::class.java)

                val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
                envelope.dotNet = false
                envelope.setOutputSoapObject(request)

//                val httpTransport = HttpTransportSE(URL)
                //czyli ten adres bedzie do zmiany jak cos
                val httpTransport = HttpTransportSE("http://192.168.217.113:8090/MapService?wsdl")
                httpTransport.debug = true // Włącz debugowanie

                //tutaj sie wykonuje żądanie
                httpTransport.call(SOAP_ACTION, envelope)


//                Log.d("SOAP Request", httpTransport.requestDump)
//                Log.d("SOAP Response", httpTransport.responseDump)

                //response
//                val response = envelope.response as String
                val response = envelope.bodyIn.toString() as String
                val extractedData = response.substringAfter("return=").substringBefore("}")
                val decodedBytes = Base64.decode(extractedData, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                runOnUiThread {
                    imgMap.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Błąd: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
    private fun addProperty(request: SoapObject, name: String, value: Any, type: Class<*>) {
        val property = PropertyInfo()
        property.name = name
        property.value = value
        property.type = type
        request.addProperty(property)
    }


}