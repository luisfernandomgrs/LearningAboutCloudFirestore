package com.stackmobile.aplicativodeteste.view.formlogin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.stackmobile.aplicativodeteste.R
import com.stackmobile.aplicativodeteste.databinding.ActivityFormLoginBinding
import com.stackmobile.aplicativodeteste.dialog.DialogLoading
import com.stackmobile.aplicativodeteste.view.formcadastro.FormCadastro
import com.stackmobile.aplicativodeteste.view.recuperarsenha.ResetKeyActivity
import com.stackmobile.aplicativodeteste.view.telaprincipal.TelaPrincipal

class FormLogin : AppCompatActivity() {
    lateinit var binding: ActivityFormLoginBinding
    private val auth = FirebaseAuth.getInstance()
    lateinit var clientLogingoogle: GoogleSignInClient
    val dialogLoading = DialogLoading(this)

    val activityResultLoggin = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == requestedOrientation) {
            val taskSignIn = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val accountLoggin = taskSignIn.getResult(ApiException::class.java)
            autenticarUsuarioGoogle(accountLoggin.idToken!!)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.googleSignInOptions()

        binding.btEntrar.setOnClickListener { view ->
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                val snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { autenticacao ->
                    if (autenticacao.isSuccessful) {
                        dialogLoading.onLoadAlertDialog()
                        Handler(Looper.getMainLooper()).postDelayed({
                            dialogLoading.onCloseAlertDialog()

                            this.goToMainActivity()
                            // após inciar a intenção de mudar de Activity... é interessante finalizar a Activity atual.
                            this.finish()
                        }, 4000)
                    }
                }.addOnFailureListener { exception ->
                    val mensagemErro = when (exception) {
                        is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres!"
                        is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha inválido(s)!"
                        is FirebaseNetworkException -> "Sem conexão com a internet!"
                        else -> "Erro ao realizar login"
                    }

                    val snackbar = Snackbar.make(view, mensagemErro, Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }
        }

        binding.txtTelaCadastro.setOnClickListener {
            val intent = Intent(this, FormCadastro::class.java)
            startActivity(intent)
        }

        binding.txtRedefinirSenha.setOnClickListener {
            val intent = Intent(this, ResetKeyActivity::class.java)
            startActivity(intent)
        }

        binding.btLoginWithGoogle.setOnClickListener {
            this.logginGoggle()
        }
    }

    private fun googleSignInOptions() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.oauth_client))
            .requestEmail()
            .build()

        clientLogingoogle = GoogleSignIn.getClient(this, gso)
    }

    private fun logginGoggle() {
        val intent = clientLogingoogle.signInIntent

        activityResultLoggin.launch(intent)
    }

    private fun autenticarUsuarioGoogle(idToken: String) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credencial).addOnCompleteListener { authenticatedUser ->
            if (authenticatedUser.isSuccessful) {
                dialogLoading.onLoadAlertDialog()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialogLoading.onCloseAlertDialog()
                    this.goToMainActivity()

                    // após inciar a intenção de mudar de Activity... é interessante finalizar a Activity atual.
                    this.finish()
                }, 4000)
            }
        }
    }

    private fun goToMainActivity() {
        val navegarTelaPrincipal = Intent(this, TelaPrincipal::class.java)
        startActivity(navegarTelaPrincipal)

        Toast.makeText(this, "Usuário logado com sucesso!", Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()

        val usuarioAtual = FirebaseAuth.getInstance().currentUser

        if (usuarioAtual != null) {
            this.goToMainActivity()
            // após inciar a intenção de mudar de Activity... é interessante finalizar a Activity atual.
            this.finish()
        }
    }
}