package uz.koinot.stadion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.databinding.ItemStadiumBinding
import uz.koinot.stadion.utils.SingleBlock

class StadiumAdapter: RecyclerView.Adapter<StadiumAdapter.VHolder>() {

    private var listener : SingleBlock<Stadium>? = null
    private val list = ArrayList<Stadium>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        ItemStadiumBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int)  = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class VHolder(val view: ItemStadiumBinding):RecyclerView.ViewHolder(view.root){
        fun bind(d:Stadium){
           view.apply {
               stdName.text = d.name
               verifyCount.text = d.countVerify.toString()
               notVerifyCount.text = d.countNotVerify.toString()
           }
            itemView.setOnClickListener {
                listener?.invoke(d)
            }
        }
    }

    fun submitList(ls: List<Stadium>){
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }

    fun setOnClickListener(block:SingleBlock<Stadium>){
        listener = block
    }
}