package com.ruviapps.simple_e_commerce_app.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruviapps.simple_e_commerce_app.model.Product
import com.ruviapps.simple_e_commerce_app.model.SelectedProduct
import com.ruviapps.simple_e_commerce_app.network.ProductApi
import kotlinx.coroutines.launch

enum class NetworkStatus {
    LOADING,
    DONE,
    ERROR
}

class HomeViewModel : ViewModel() {

    private val _status = MutableLiveData<NetworkStatus>()
    val status: LiveData<NetworkStatus> get() = _status

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _totalAmount = MutableLiveData<Int>().apply {
        value = 0
    }
    val totalAmount: LiveData<Int> get() = _totalAmount

    init {
        getProductsList()
    }

    private fun getProductsList() {
        viewModelScope.launch {
            _status.value = NetworkStatus.LOADING
            try {
                _products.value = ProductApi.retrofitService.getProducts()
                _status.value = NetworkStatus.DONE

            } catch (ex: Exception) {
                _status.value = NetworkStatus.ERROR
                _products.value = ArrayList()
            }
        }
    }

    private var hashMap = hashMapOf<Product, Int>()

    private var _selectedHashMapList = MutableLiveData<HashMap<Product, Int>>().apply {
        hashMapOf<Product, Int>()
    }
    val selectedHashMapList: LiveData<HashMap<Product, Int>> get() = _selectedHashMapList




    fun addProductToCart(product: Product, qty: Int) {
            if (hashMap.contains(product))
            {
                if(hashMap.getValue(product).plus(qty) < product.stock)
                    hashMap[product] = hashMap[product]?.plus(qty) ?: qty;
            }
            else{
                hashMap[product] = qty
            }


                _selectedHashMapList.value = hashMap

       //  _selectedProducts.value = productList
        getCartAmount()

    }


    fun removeProductFromCart(product: Product) {
        if(hashMap.containsKey(product))
            hashMap.remove(product)

        _selectedHashMapList.value = hashMap
        getCartAmount()
    }


    private fun getCartAmount() {
        var total = 0
       hashMap.forEach { (pr, value) ->
                     total += (pr.price * value)
       }
        _totalAmount.value = total
    }

    fun clearCart() {
        hashMap = hashMapOf()
        _totalAmount.value = 0
        _selectedHashMapList.value = hashMap
    }
}
