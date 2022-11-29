package com.stackmobile.aplicativodeteste.view.telaprincipal

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stackmobile.aplicativodeteste.R
import com.stackmobile.aplicativodeteste.databinding.ActivityTelaPrincipalBinding
import com.stackmobile.aplicativodeteste.view.formlogin.FormLogin

class TelaPrincipal : AppCompatActivity() {
    lateinit var binding: ActivityTelaPrincipalBinding
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btLogouth.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            var intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
            finish()
        }

        binding.btInsertDB.setOnClickListener { view ->
            val usersMap = hashMapOf(
                "name" to "XPS 8940",
                "lastName" to "Desktop",
                "email" to "xps8940@gmail.com",
                "createAt" to 2022
            )
            db.collection("Users").document("3").set(usersMap).addOnCompleteListener {
                Log.d("db", "Success on create register to new data.")

                val snackbar = Snackbar.make(view, "Success on create or update record!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.BLUE)
                snackbar.show()
            }.addOnFailureListener {
                val snackbar = Snackbar.make(view, "Failure on create record!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }
        }

        binding.btUpdateDB.setOnClickListener { view ->
            /*
            * Multiples fields on Update record
            * Learning basic CRUD on Cloud Firestore
             */
            db.collection("Users")
                .document("3")
                .update("name","XPS 8940, RTX 2060",
                    "createAt", 2021)
                .addOnCompleteListener {

                    val snackbar = Snackbar.make(view, "Success on update record!", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.BLUE)
                    snackbar.show()
            }.addOnFailureListener {
                val snackbar = Snackbar.make(view, "Failure on update record!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }
        }

        binding.btReadDB.setOnClickListener {
            db.collection("Users").document("3").addSnapshotListener { document, error ->
                if (document != null) {
                    //val createAt = document.getLong("createAt")

                    binding.txtResultDataFromDB.text = "Name: " + document.getString("name") + "\n" +
                            "lastName: " + document.getString("lastName") + "\n" +
                            "E-mail: " + document.getString("email") + "\n" +
                            "Create At: " + document.getLong("createAt").toString()
                }
            }
        }
    }
}