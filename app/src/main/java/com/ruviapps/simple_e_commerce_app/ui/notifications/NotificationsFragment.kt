package com.ruviapps.simple_e_commerce_app.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.ruviapps.simple_e_commerce_app.R
import com.ruviapps.simple_e_commerce_app.databinding.FragmentNotificationsBinding
import com.ruviapps.simple_e_commerce_app.databinding.SelectedProductLayoutBinding
import com.ruviapps.simple_e_commerce_app.model.SelectedProduct
import com.ruviapps.simple_e_commerce_app.model.toDomainProduct
import com.ruviapps.simple_e_commerce_app.model.toSelectedProduct
import com.ruviapps.simple_e_commerce_app.ui.home.HomeViewModel
import com.ruviapps.simple_e_commerce_app.ui.home.NetworkStatus

class NotificationsFragment : Fragment() {

    private var checkOutList = mutableListOf<SelectedProduct>()
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val homeViewModel : HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.showDetailsButton.setOnClickListener {
            if(!binding.selectedProductRecycler.isVisible){
                binding.selectedProductRecycler.visibility = View.VISIBLE
                binding.showDetailsButton.text = getString(R.string.hide_details)

            }

            else{
                binding.selectedProductRecycler.visibility = View.GONE
                binding.showDetailsButton.text = getString(R.string.show_details)

            }

        }

        binding.clearCart.setOnClickListener {
            homeViewModel.clearCart()
        }

        val adapter = SelectedProductsAdapter(object : SelectedProductsAdapter.OnClickListener{
            override fun onProductClick(pr: SelectedProduct) {
                homeViewModel.removeProductFromCart(pr.toDomainProduct())
            }
        })

        homeViewModel.selectedHashMapList.observe(viewLifecycleOwner){
           val list = mutableListOf<SelectedProduct>()
            it.forEach { (p, q) ->
                list.add(p.toSelectedProduct(q))
            /* list.add(SelectedProduct(p.id,p.title,p.description,p.price,p.discountPercentage,p.rating,p.stock,p.brand,p.category,p.thumbnail
                ,q))*/
            }
            adapter.submitList( list)
            checkOutList = list
            binding.selectedProductRecycler.adapter = adapter
        }

        homeViewModel.totalAmount.observe(viewLifecycleOwner){
            binding.textNotifications.text = getString(R.string.total_amount,it.toString())
        }



        val noItemToast =Toast.makeText(requireContext(),getString(R.string.no_product_warning),Toast.LENGTH_SHORT)
        val noAddressToast = Toast.makeText(requireContext(),getString(R.string.missing_address_warning),Toast.LENGTH_SHORT)

        binding.checkoutButton.setOnClickListener {
            val itemCount = binding.selectedProductRecycler.adapter?.itemCount ?: 0
            if(itemCount == 0){
                noItemToast.cancel()
                noItemToast.show()
                return@setOnClickListener
            }else  if(binding.addressField.text.isNullOrEmpty()){
                noAddressToast.cancel()
                noAddressToast.show()
                return@setOnClickListener
            }

            homeViewModel.checkOutOrder(checkOutList)

            homeViewModel.status.observe(viewLifecycleOwner){
                when(it){
                    NetworkStatus.DONE -> {
                        Toast.makeText(requireContext(),"Uploaded Successfully",Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        findNavController().navigate(R.id.action_navigation_notifications_to_navigation_dashboard)
                    }
                    NetworkStatus.ERROR ->{
                        Toast.makeText(requireContext(),"Error Occurred while Uploading ",Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }
                     NetworkStatus.LOADING ->{
                            binding.progressBar.visibility = View.VISIBLE
                     }
                    else ->
                    {
                        Toast.makeText(requireContext(),"Something Went Wrong ! Contact Developer Asap",Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE

                    }
                }
            }

        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding.selectedProductRecycler.adapter = null
        _binding = null
    }
}


class SelectedProductsAdapter(private val onClickListener: OnClickListener) : ListAdapter<SelectedProduct,
        SelectedProductsAdapter.MyViewHolder>(DiffCallback) {

    class MyViewHolder(private var binding: SelectedProductLayoutBinding, private val clickListener: OnClickListener): RecyclerView.ViewHolder(binding.root){
        fun bind(pr : SelectedProduct){
            binding.productImageView.load(pr.thumbnail){
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                transformations(CircleCropTransformation())
            }
            binding.productNameTv.text = pr.title
            binding.ratingBar.rating = pr.rating
            binding.productPriceTv.text = binding.root.context.getString(R.string.cart_product_rate,pr.price.toString(),pr.cartQyt)
            binding.buyButton.setOnClickListener {
                clickListener.onProductClick(pr)
            }
        }
        companion object{
            fun from(parent : ViewGroup,clickListener: OnClickListener) : MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SelectedProductLayoutBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding,clickListener)
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<SelectedProduct>(){
        override fun areItemsTheSame(oldItem: SelectedProduct, newItem: SelectedProduct): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: SelectedProduct, newItem: SelectedProduct): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent,onClickListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pr = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onProductClick(pr)
        }
        holder.bind(pr)
    }

    interface OnClickListener{
        fun onProductClick(pr : SelectedProduct)
    }

}