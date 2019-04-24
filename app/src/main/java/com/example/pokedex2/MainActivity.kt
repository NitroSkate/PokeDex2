package com.example.pokedex2

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.pokedex2.pojo.Pokemon
import com.example.pokedex2.utilities.NetworkUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerManager : RecyclerView.LayoutManager
    private lateinit var pokeAdapter : PokemonAdapter
    var pokelist = mutableListOf<Pokemon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FetchPokemonTask().execute("")
        //TY
        initRecycler(pokelist)
    }

    fun initRecycler (pokemon : MutableList<Pokemon>){
        recyclerManager = LinearLayoutManager(this)
        pokeAdapter = PokemonAdapter(pokemon, {item : Pokemon -> clickitem(item)})

        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = recyclerManager
            adapter = pokeAdapter
        }

    }


    private fun clickitem(item: Pokemon){
        startActivity(Intent(this, Datos::class.java).putExtra("clave", item.tipo))
    }

    private inner class FetchPokemonTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg query: String): String {
            if (query.isNullOrEmpty()) return ""

            val ID = query[0]
            val pokeAPI = NetworkUtils().buildUrl("pokemon", ID)

            return try {
                NetworkUtils().getResponseFromHttpUrl(pokeAPI)
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }
        }

        override fun onPostExecute(pokemonInfo: String){
            if (!pokemonInfo.isEmpty()) {
                val root = JSONObject(pokemonInfo)
                val results = root.getJSONArray("results")
                for (i in 1..900){
                    val result = JSONObject(results[i].toString())
                    /*MutableList(20) { i ->
                        val result = JSONObject(results[i].toString())
                        Pokemon(
                            result.getString("name").capitalize(),
                            result.getString("url")}*/
                    pokelist.add(Pokemon(result.getString("name").capitalize(), result.getString("url")))
                    Log.d("alv", pokelist[0].nombre)
                    pokeAdapter.notifyDataSetChanged()
                }
            } else {
                MutableList(20) { i ->
                    Pokemon("N/A",
                        "N/A")
                }
            }
        }
    }

}
