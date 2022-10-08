package com.ruviapps.simple_e_commerce_app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ruviapps.simple_e_commerce_app.MainActivity
import com.ruviapps.simple_e_commerce_app.R
import com.ruviapps.simple_e_commerce_app.databinding.FragmentHomeBinding
import com.ruviapps.simple_e_commerce_app.model.Product
import com.ruviapps.simple_e_commerce_app.model.SelectedProduct

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val homeViewModel : HomeViewModel by activityViewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getStoreName()
        homeViewModel.storeInfo.observe(viewLifecycleOwner){
            binding.storeName.text = it[0].store
        }

        homeViewModel.status.observe(viewLifecycleOwner){
            when(it){
            NetworkStatus.LOADING ->
            {binding.progressBar.visibility = View.VISIBLE
                binding.errorView.visibility = View.GONE

            }
            NetworkStatus.DONE -> {
                binding.progressBar.visibility = View.GONE
                binding.errorView.visibility = View.GONE
            }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.errorView.text = getString(R.string.error_text)
                    binding.errorView.visibility = View.VISIBLE
                }

            }

        }

        binding.textHome.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_notifications)
        }


        val adapter = ProductsAdapter(object : ProductsAdapter.OnClickListener{
            override fun onProductClick(pr: Product, qytToAdd : Int) {
                homeViewModel.addProductToCart(pr,qytToAdd)
                Toast.makeText(requireContext(),getString(R.string.cart_addition_confirm_message,qytToAdd),Toast.LENGTH_SHORT).show()
            }

        })
        val list = createProductListFromJson(requireContext())
        Log.d("myTag","Parsing manually then the List is \n $list")
           // adapter.submitList(list)

        val recyclerView = binding.recyclerView

    homeViewModel.products.observe(viewLifecycleOwner){
            adapter.submitList(it)
            recyclerView.adapter = adapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}