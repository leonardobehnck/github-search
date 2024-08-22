package br.com.igorbag.githubsearch.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
  RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

  var itemLister: (Repository) -> Unit = {}
  var btnShareLister: (Repository) -> Unit = {}


  //Cria uma nova view
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view =
      LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
    return ViewHolder(view)
  }

  //Pega o conteudo da view e troca pela informacao de item de uma lista
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    holder.name.text = repositories[position].name

    //Implementa a funcionalidade de clique no item
    holder.itemView.setOnClickListener {
      itemLister(repositories[position])
      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repositories[position].htmlUrl))
      holder.itemView.context.startActivity(intent)
    }

    //Implementa a funcionalidade de clique no botão de compartilhar
    holder.shared.setOnClickListener {
      btnShareLister(repositories[position])
      val clipboard =
        holder.itemView.context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
      val clip =
        android.content.ClipData.newPlainText("Repository URL", repositories[position].htmlUrl)
      clipboard.setPrimaryClip(clip)
      Toast.makeText(holder.itemView.context, "Copiado!", android.widget.Toast.LENGTH_SHORT).show()
    }
  }

  //Pega a quantidade de repositorios da lista
  override fun getItemCount(): Int = repositories.size

  //Cria a viewholder
  class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView
    val shared: ImageView

    init {
      name = view.findViewById(R.id.tv_repo_name)
      shared = view.findViewById(R.id.iv_share)
    }
  }
}


