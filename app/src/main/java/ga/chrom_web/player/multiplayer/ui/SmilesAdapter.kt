package ga.chrom_web.player.multiplayer.ui

import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.SmilesLoader
import ga.chrom_web.player.multiplayer.di.App
import java.util.ArrayList
import javax.inject.Inject

class SmilesAdapter : RecyclerView.Adapter<SmilesAdapter.SmilesViewHolder> {

    private var items: ArrayList<Pair<String, String>>
    var mIsBigSmiles: Boolean = false;
    var onSmileClickListener: (String) -> Unit = {}
    @Inject
    lateinit var smilesLoader: SmilesLoader

    constructor(items: ArrayList<Pair<String, String>>) : super() {
        App.getComponent().inject(this)
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmilesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_smiles, parent, false)
        return SmilesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmilesViewHolder, position: Int) {

        smilesLoader.loadSmile(items[position].first, onSmileReadyListener = { file ->
            Picasso.with(holder.itemView.context).load(file).into(holder.imgSmile)
        })

        holder.imgSmile.setOnClickListener {
            var smileToSend = items[position].first
            if (mIsBigSmiles) {
                smileToSend += "Big"
            }
            // add empty space to make it look better
            smileToSend += " "
            onSmileClickListener.invoke(smileToSend)
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