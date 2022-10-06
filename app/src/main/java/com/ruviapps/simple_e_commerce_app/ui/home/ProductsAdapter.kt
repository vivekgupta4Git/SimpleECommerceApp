package com.ruviapps.simple_e_commerce_app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ruviapps.simple_e_commerce_app.R
import com.ruviapps.simple_e_commerce_app.databinding.FragmentDashboardBinding
import com.ruviapps.simple_e_commerce_app.model.Product

class ProductsAdapter(private val onClickListener: OnClickListener) : ListAdapter<Product,
        ProductsAdapter.MyViewHolder>(DiffCallback) {

             class MyViewHolder(private var binding: FragmentDashboardBinding): RecyclerView.ViewHolder(binding.root){
                fun bind(pr : Product){

                }
                companion object{
                    fun from(parent : ViewGroup) : MyViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = FragmentDashboardBinding.inflate(layoutInflater,parent,false)
                        return MyViewHolder(binding)
                    }
                }
            }

    object DiffCallback :DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pr = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onProductClick(pr)
        }
        holder.bind(pr)
    }

    interface OnClickListener{
        fun onProductClick(pr : Product)
    }

}

