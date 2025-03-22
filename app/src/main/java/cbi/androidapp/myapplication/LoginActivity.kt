package cbi.androidapp.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cbi.androidapp.myapplication.network.ApiClient
import cbi.androidapp.myapplication.network.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        println("🔹 [DEBUG] Mengirim request login dengan email: $email dan password: $password")

        ApiClient.instance.loginUser(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                println("🔹 [DEBUG] Response Code: ${response.code()}")
                println("🔹 [DEBUG] Response Body: ${response.body()}")

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse?.statusCode == 1) {
                        val token = loginResponse.data?.api_token ?: ""
                        saveSession(email, token)

                        Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Gagal: ${loginResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Terjadi Kesalahan! Cek Email & Password.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                println("❌ [ERROR] Gagal Terhubung ke Server: ${t.message}")
                Toast.makeText(this@LoginActivity, "Gagal Terhubung ke Server: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun saveSession(email: String, token: String) {
        sharedPreferences.edit {
            putString("EMAIL", email)
            putString("TOKEN", token)
        }
        val savedEmail = sharedPreferences.getString("EMAIL", null)
        val savedToken = sharedPreferences.getString("TOKEN", null)
        println("SESSION: Email = $savedEmail, Token = $savedToken")
    }
}
