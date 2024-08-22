package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

  lateinit var userName: EditText
  lateinit var btnSend: Button
  lateinit var listRepositories: RecyclerView
  lateinit var githubApi: GitHubService

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setupView()
    setupListeners()
    showUserName()
    setupRetrofit()
  }

  //Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
  fun setupView() {
    userName = findViewById(R.id.et_nome_usuario)
    btnSend = findViewById(R.id.btn_confirmar)
    listRepositories = findViewById(R.id.rv_lista_repositories)
  }

  //Metodo responsavel por configurar os listeners click da tela
  private fun setupListeners() {
    btnSend.setOnClickListener {
      val userName = userName.text.toString()
      saveUserLocal(userName)
      getAllReposByUserName(userName)
    }
  }


  //Salvar o usuario preenchido no EditText utilizando uma SharedPreferences
  private fun saveUserLocal(user: String) {
    val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()) {
      putString(getString(R.string.saved_user), user)
      apply()
    }
  }

  //Recuperar o usuario salvo na SharedPreferences
  private fun getSharedPref(): String? {
    val sharedPref = getPreferences(Context.MODE_PRIVATE)
    return sharedPref.getString(getString(R.string.saved_user), "")
  }

  //Metodo responsavel por exibir o usuario salvo na SharedPreferences no EditText
  private fun showUserName() {
    val sharedPref = getSharedPref()
    if (sharedPref != null) {
      userName.setText(sharedPref)
    }
  }

  //Metodo responsavel por fazer a configuracao base do Retrofit
  fun setupRetrofit() {
    val retrofit = Retrofit.Builder()
      .baseUrl("https://api.github.com/")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    githubApi = retrofit.create(GitHubService::class.java)

  }

  //Metodo responsavel por buscar todos os repositorios do usuario fornecido
  fun getAllReposByUserName(user: String) {
    githubApi.getAllRepositoriesByUser(user).enqueue(object : Callback<List<Repository>> {
      override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
        Toast.makeText(this@MainActivity, R.string.response_error, Toast.LENGTH_SHORT).show()
      }

      override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
        response.body()?.let {
          setupAdapter(it)
        }
      }
    }
    )
  }

  //Metodo responsavel por realizar a configuracao do adapter
  fun setupAdapter(list: List<Repository>) {
    val adapter = RepositoryAdapter(list)
    listRepositories.adapter = adapter
  }
}