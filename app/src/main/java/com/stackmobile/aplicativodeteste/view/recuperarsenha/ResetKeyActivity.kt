package com.stackmobile.aplicativodeteste.view.recuperarsenha

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.stackmobile.aplicativodeteste.R
import com.stackmobile.aplicativodeteste.databinding.ActivityResetKeyBinding

class ResetKeyActivity : AppCompatActivity() {
    lateinit var binding: ActivityResetKeyBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btRedefinirSenha.setOnClickListener { view ->
            val email = binding.editEmail.text.toString()

            if (email.isEmpty()) {
                val snackbar = Snackbar.make(view, "Preencha o campo de e-mail!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { actionResetKey ->
                    if (actionResetKey.isSuccessful) {
                        val snackbar = Snackbar.make(view, "Verifique seu e-mail, para continuar!", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.BLUE)
                        snackbar.show()
                    }
                }.addOnFailureListener {exception ->
                    val mensagemErro = when(exception) {
                        is FirebaseAuthInvalidCredentialsException -> "Digite um e-mail válido!"
                        is FirebaseAuthUserCollisionException -> "E-mail ou senha inválido(s)!"
                        is FirebaseNetworkException -> "Sem conexão com a internet!"
                        else -> "Não foi possível redefinir sua senha!"
                    }

                    val snackbar = Snackbar.make(view, mensagemErro, Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }
        }
    }
}