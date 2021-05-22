package uz.koinot.stadion.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.databinding.ItemStadiumBinding
import uz.koinot.stadion.utils.SingleBlock
import uz.koinot.stadion.utils.setForceShowIcon

class StadiumAdapter : RecyclerView.Adapter<StadiumAdapter.VHolder>() {

    private var listener: SingleBlock<Stadium>? = null
    private var listenerAddImage: SingleBlock<Stadium>? = null
    private var updateListener: SingleBlock<Stadium>? = null
    private var imageListener: ((Stadium, Int) -> Unit)? = null
    private val list = ArrayList<Stadium>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VHolder(
        ItemStadiumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: VHolder, position: Int) = holder.bind(list[position])

    override fun getItemCount() = list.size

    inner class VHolder(val view: ItemStadiumBinding) : RecyclerView.ViewHolder(view.root) {
        @RequiresApi(Build.VERSION_CODES.Q)
        fun bind(d: Stadium) {
            view.apply {
                stdName.text = d.name
                verifyCount.text = d.countVerify.toString()
                notVerifyCount.text = d.countNotVerify.toString()
                val adapter = RvImageAdapter(d)
                rvImages.adapter = adapter

                adapter.setOnClickListener { stadium, pos ->
                    imageListener?.invoke(stadium, pos)
                }
                btnMore.setOnClickListener {
                    val popup = PopupMenu(it.context, it)
                    popup.inflate(R.menu.popup_menu)
                    popup.setForceShowIcon()
                    popup.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.update_menu -> {
                                updateListener?.invoke(d)
                            }
                            R.id.add_image_menu -> {
                                if(list.size < 10)
                                listenerAddImage?.invoke(d)
                            }
                            else -> Unit
                        }
                        true
                    }
                    popup.show()
                }
            }
            itemView.setOnClickListener {
                listener?.invoke(d)
            }
            itemView.setOnLongClickListener {
                updateListener?.invoke(d)
                true
            }
        }
    }

    fun submitList(ls: List<Stadium>) {
        list.clear()
        list.addAll(ls)
        notifyDataSetChanged()
    }

    fun setOnClickListener(block: SingleBlock<Stadium>) {
        listener = block
    }

    fun setOnUpdateClickListener(block: SingleBlock<Stadium>) {
        updateListener = block
    }

    fun setOnImageClickListener(block: (Stadium, Int) -> Unit) {
        imageListener = block
    }

    fun setOnAddImageClickListener(block: (Stadium) -> Unit) {
        listenerAddImage = block
    }
}