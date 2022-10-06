package com.ruviapps.simple_e_commerce_app.ui.home

import android.net.Network
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

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    private val _status = MutableLiveData<NetworkStatus>()
    val status : LiveData<NetworkStatus> get() = _status

    private val _products = MutableLiveData<List<Product>>()
    val products : LiveData<List<Product>> get() = _products

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

}
