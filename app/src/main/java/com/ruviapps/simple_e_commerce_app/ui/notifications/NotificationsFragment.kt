package com.ruviapps.simple_e_commerce_app.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ruviapps.simple_e_commerce_app.R
import com.ruviapps.simple_e_commerce_app.databinding.FragmentNotificationsBinding
import com.ruviapps.simple_e_commerce_app.model.Product
import com.ruviapps.simple_e_commerce_app.ui.home.HomeViewModel
import com.ruviapps.simple_e_commerce_app.ui.home.SelectedProductsAdapter

class NotificationsFragment : Fragment() {

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

        val adapter = SelectedProductsAdapter(object : SelectedProductsAdapter.OnClickListener{
            override fun onProductClick(pr: Product) {
                homeViewModel.removeProductFromCart(pr)
            }
        })
        homeViewModel.selectedProducts.observe(viewLifecycleOwner){
            adapter.submitList(it)
            binding.selectedProductRecycler.adapter = adapter
        }
        homeViewModel.totalAmount.observe(viewLifecycleOwner){
            binding.textNotifications.text = getString(R.string.total_amount,it.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.selectedProductRecycler.adapter = null
        _binding = null
    }
}