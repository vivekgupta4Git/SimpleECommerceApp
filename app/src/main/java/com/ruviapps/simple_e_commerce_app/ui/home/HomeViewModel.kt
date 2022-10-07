package com.ruviapps.simple_e_commerce_app.ui.home

import android.net.Network
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruviapps.simple_e_commerce_app.model.Product
import com.ruviapps.simple_e_commerce_app.network.ProductApi
import com.ruviapps.simple_e_commerce_app.network.ProductApiService
import kotlinx.coroutines.launch
import java.lang.Exception

enum class NetworkStatus{
    LOADING,
    DONE,
    ERROR
}

class HomeViewModel : ViewModel() {

    private val _selectedProducts = MutableLiveData<List<Product>>().apply {
    value =  mutableListOf()
    }
    val selectedProducts : LiveData<List<Product>> get() = _selectedProducts

    private val _status = MutableLiveData<NetworkStatus>()
    val status : LiveData<NetworkStatus> get() = _status

    private val _products = MutableLiveData<List<Product>>()
    val products : LiveData<List<Product>> get() = _products

    private val _totalAmount = MutableLiveData<Int>().apply {
        value = 0
    }
    val totalAmount : LiveData<Int> get() = _totalAmount

    init {
        getProductsList()
    }
    private fun getProductsList(){
        viewModelScope.launch {
            _status.value = NetworkStatus.LOADING
            try {
                _products.value = ProductApi.retrofitService.getProducts()
                _status.value = NetworkStatus.DONE

            }catch (ex : Exception){
                _status.value = NetworkStatus.ERROR
                _products.value = ArrayList()
            }
        }
    }

    private var productList = mutableListOf<Product>()

    fun addProductToCart(product: Product){
        productList.add(product)
        _selectedProducts.value = productList
        getCartAmount()
    }

    fun removeProductFromCart(product: Product){
        productList.contains(product).let {
            if(it){
                productList.remove(product)
                _selectedProducts.value = productList
            }
        }
        getCartAmount()
    }



    private fun getCartAmount() {
        var total = 0
        productList.forEach {
            total +=it.price
        }
        _totalAmount.value = total
    }

    fun clearCart()
    {
        productList = mutableListOf()
        _totalAmount.value = 0
        _selectedProducts.value = productList
    }
}
