package com.ruviapps.simple_e_commerce_app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ruviapps.simple_e_commerce_app.databinding.FragmentHomeBinding
import com.ruviapps.simple_e_commerce_app.model.Product

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ProductsAdapter(object : ProductsAdapter.OnClickListener{
            override fun onProductClick(pr: Product) {
                Toast.makeText(requireContext(),"Product id : ${pr.id}",Toast.LENGTH_SHORT).show()
            }

        })
        val recyclerView = binding.recyclerView
        homeViewModel.products.observe(viewLifecycleOwner){
            adapter.submitList(it)
            recyclerView.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        binding.recyclerView.adapter = null
    }
}