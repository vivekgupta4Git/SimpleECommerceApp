package com.ruviapps.simple_e_commerce_app.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.ruviapps.simple_e_commerce_app.R
import com.ruviapps.simple_e_commerce_app.databinding.SelectedProductLayoutBinding
import com.ruviapps.simple_e_commerce_app.databinding.SingleProductLayoutBinding
import com.ruviapps.simple_e_commerce_app.model.Product
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder

class ProductsAdapter(private val onClickListener: OnClickListener) : ListAdapter<Product,
        ProductsAdapter.MyViewHolder>(DiffCallback) {

             class MyViewHolder(private var binding: SingleProductLayoutBinding,private val clickListener: OnClickListener): RecyclerView.ViewHolder(binding.root){
                fun bind(pr : Product){
                        binding.productImageView.load(pr.thumbnail){
                            crossfade(true)
                            placeholder(R.drawable.ic_launcher_foreground)
                            transformations(CircleCropTransformation())
                        }
                        binding.quantityEditTv.setText("1")
                        binding.productNameTv.text = pr.title
                        binding.ratingBar.rating = pr.rating
                        binding.discountTv.text = binding.root.context.getString(R.string.product_discount,pr.discountPercentage.toString())
                        binding.productPriceTv.text = binding.root.context.getString(R.string.product_rate,pr.price.toString())

                        binding.buyButton.setOnClickListener {
                            val qty =   binding.quantityEditTv.text.toString().toInt()
                            clickListener.onProductClick(pr,qty)
                        }

                        binding.addButton.setOnClickListener {
                            var q = binding.quantityEditTv.text.toString().toInt()
                            q += 1
                            if(q < pr.stock)
                                binding.quantityEditTv.setText(q.toString())
                            else
                                binding.quantityEditTv.setText(pr.stock.toString())
                        }

                        binding.removeButton.setOnClickListener {
                            var q = binding.quantityEditTv.text.toString().toInt()
                            q -= 1
                            if(q > 0)
                            binding.quantityEditTv.setText(q.toString())
                            else
                                binding.quantityEditTv.setText("1")
                        }


                }
                companion object{
                    fun from(parent : ViewGroup,clickListener: OnClickListener) : MyViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = SingleProductLayoutBinding.inflate(layoutInflater,parent,false)
                        return MyViewHolder(binding,clickListener)
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
       return MyViewHolder.from(parent,onClickListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pr = getItem(position)
        holder.bind(pr)
    }

    interface OnClickListener{
        fun onProductClick(pr : Product,qytToAdd :Int)
    }

}




private fun readJsonFromFile(context: Context) : String{
    var inputStream : InputStream? = null
    val builder  = StringBuilder()
    try {
        inputStream = context.resources.openRawResource(R.raw.products)
        val bufferedReader = BufferedReader(
            InputStreamReader(inputStream,"UTF-8")
        )
        while(bufferedReader.readLine()!=null){
            builder.append(bufferedReader.readLine())
        }
    }catch (ex : Exception){
       // Log.d("myTag","Error =${ex.localizedMessage}")
    }finally {
        inputStream?.close()
    }
    return builder.toString()
}
/*
 val id : Int,
    val title : String?,
    val description : String?,
    val price : Int,
    val discountPercentage : Float,
    val rating : Float,
    val stock : Int,
    val brand : String,
    val category : String?
 */
fun createProductListFromJson(context: Context) : List<Product>{
    val mutableList = mutableListOf<Product>()
    try {
        val dataString = readJsonFromFile(context)
        val jsonArray = JSONArray(dataString)
        Log.d("myTag","json Array is $jsonArray")
        for( i in 0 until jsonArray.length()){
            val jsonObject = jsonArray.optJSONObject(i)
            Log.d("myTag","Json Object at $i is $jsonObject")
            val pId = jsonObject.getInt("pid")
            val title = jsonObject.getString("title")
            val descrip = jsonObject.getString("description")
            val price = jsonObject.getInt("price")
            val discount = jsonObject.getDouble("discountPercentage")
            val rating = jsonObject.getDouble("rating")
            val stock = jsonObject.getInt("stock")
            val brand = jsonObject.getString("brand")
            val category = jsonObject.getString("category")
            val thumbnail = jsonObject.getString("thumbnail")
            mutableList.add(Product(pId,title,descrip,price,discount.toFloat(),rating.toFloat(),stock,brand,category,thumbnail))
        }
    }catch (ex : Exception)
    {
        ex.printStackTrace()
    }
    return mutableList
}