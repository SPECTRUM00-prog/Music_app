package com.example.music_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnInicio: Button
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var auth: FirebaseAuth

    private lateinit var credentialManager: CredentialManager

    private lateinit var btnGoogle: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        etCorreo = findViewById(R.id.etCorreo)

        etPassword = findViewById(R.id.etPassword)

        btnInicio = findViewById(R.id.btnIngresar)

        dbHelper = DatabaseHelper(this)

        btnGoogle = findViewById(R.id.btnGoogle)

        auth = FirebaseAuth.getInstance()

        credentialManager = CredentialManager.create(this)

        //Evento para iniciar sesion
        btnInicio.setOnClickListener {
            //TODO: Agregar metodo de incio de sesion
            iniciarSesion()

        }

        tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    //Metodo para iniciar sesion
    private fun iniciarSesion() {
        //Acceder a la informacion que escribe el usuario
        val correo = etCorreo.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //Llamar al metodo de inicio de sesion en la BD
        val resultado = dbHelper.iniciarSesion(correo, password)

        if (resultado) {
            //Si la sesion es correcta
            Toast.makeText(this, "Bienvenido :D", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            //Cerrar la pantalla actual
            finish()
        } else {
            //Si la sesion es incorrecta
            Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show()
        }

    }

    // 3. MÉTODO PARA AUTENTICAR EN FIREBSAE
    private fun autenticarFirebase(idToken: String) {

        val credencialFirebase = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credencialFirebase).addOnCompleteListener(this) {

                inicio ->

            //Validar si el iniciio de sesion con Firebase es exitoso
            if (inicio.isSuccessful) {
                //Variable para obtener toda la info del usuario
                val usuario = auth.currentUser

                Toast.makeText(this, "Bienvenido ${usuario?.displayName}", Toast.LENGTH_SHORT)
                    .show()

                val intent = Intent(this, HomeActivity::class.java)

                startActivity(intent)

                finish()

            } else {
                Toast.makeText(this, "Error al validar con Firebase", Toast.LENGTH_SHORT).show()
            }

        }
    }

    // 4. MÉTODO PARA PROCESAR LA CREDENCIAL
    private fun procesarCredencial(credential: Credential) {

        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

            try {

                //Variable para obtener la credencial de google
                val credencialGoogle = GoogleIdTokenCredential.createFrom(
                    credential.data
                )

                autenticarFirebase(credencialGoogle.idToken)

            } catch (e: GoogleIdTokenParsingException) {

                Toast.makeText(this, "Credencial Invalida", Toast.LENGTH_SHORT).show()


            }
        }
    }

    // 5. MÉTODO PARA INICIAR SESIÓN
    private fun iniciarSesionGoogle() {

        val inicioGoogle = GetSignInWithGoogleOption.Builder(
            getString(R.string.default_web_client_id)
        ).build()

        val solicitud = GetCredentialRequest.Builder()
            .addCredentialOption(inicioGoogle)
            .build()

        lifecycleScope.launch {
            try {

            } catch (e: GetCredentialException) {

            }
        }

    }

}
