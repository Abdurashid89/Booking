package uz.koinot.stadion.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.databinding.ItemOrderBinding
import uz.koinot.stadion.utils.SingleBlock

class OrderAdapter: RecyclerView.Adapter<OrderAdapter.VHolder>() {

    private var listener : SingleBlock<Order>? = null
    private var acceptListener : SingleBlock<Order>? = null
    private var rejectListener : SingleBlock<Order>? = null
    private val list = ArrayList<Order>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        ItemOrderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )

    override fun onBindViewHolder(holder: VHolder, position: Int)  = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class VHolder(val view: ItemOrderBinding): RecyclerView.ViewHolder(view.root){
        @SuppressLint("SetTextI18n")
        fun bind(d: Order){
            view.apply {
               userName.text = "${d.firstName} ${d.lastName}"
               startDate.text = d.startDate
               endDate.text = d.endDate
               day.text = d.time
               phone1.text = d.phoneNumber
               phone2.text = d.originalPhoneNumber

                btnAccept.setOnClickListener {
                    acceptListener?.invoke(d)
                }
                btnReject.setOnClickListener {
                    rejectListener?.invoke(d)
                }

                layoutAccept.isVisible = !d.active

            }

        }
    }

    fun submitList(ls: List<Order>){
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }

    fun setOnClickListener(block:SingleBlock<Order>){
        listener = block
    }

    fun setOnAcceptListener(block:SingleBlock<Order>){
        acceptListener = block
    }

    fun setOnRejectListener(block:SingleBlock<Order>){
        rejectListener = block
    }
}