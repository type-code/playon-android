package ga.chrom_web.player.multiplayer.ui

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.Utils
import java.io.File
import java.util.ArrayList

class SmilesAdapter : RecyclerView.Adapter<SmilesAdapter.SmilesViewHolder> {

    private var items: ArrayList<Pair<String, String>>
    var onSmileClickListener: (String) -> Unit = {}

    constructor(items: ArrayList<Pair<String, String>>) : super() {
        this.items = items
    }

    constructor() : super() {
        this.items = ArrayList()
    }

//    fun setOnSmileClickListener(onSmileClickListener: (String) -> Unit ) {
//        this.onSmileClickListener = onSmileClickListener
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmilesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_smiles, parent, false)
        return SmilesViewHolder(view)
    }

    fun addAll(smiles: ArrayList<Pair<String, String>>) {
        items.addAll(smiles)
    }

    override fun onBindViewHolder(holder: SmilesViewHolder, position: Int) {

        Picasso.with(holder.itemView.context).load("file:" + items[position].second).into(holder.imgSmile)
        holder.imgSmile.setOnClickListener {
            onSmileClickListener.invoke(items[position].first)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class SmilesViewHolder : RecyclerView.ViewHolder {

        var imgSmile: ImageView

        constructor(itemView: View) : super(itemView) {
            imgSmile = itemView.findViewById(R.id.imgSmile);
        }
    }

}