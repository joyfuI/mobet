package maw.mobet.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import maw.mobet.R
import maw.mobet.RetrofitClient
import maw.mobet.api.HomeListItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import splitties.resources.appTxt
import splitties.toast.toast

class HomeViewModel() : ViewModel() {
    private val _list = MutableLiveData<List<HomeListItem>>().apply {
        loadData()
    }

    val list: LiveData<List<HomeListItem>>
        get() = _list

    fun loadData() {
        val service = RetrofitClient.getInstance()
        val dataCall = service.homeList()
        dataCall.enqueue(object : Callback<List<HomeListItem>> {
            override fun onResponse(
                call: Call<List<HomeListItem>>, response: Response<List<HomeListItem>>
            ) {
                _list.value = response.body() ?: emptyList()
            }

            override fun onFailure(call: Call<List<HomeListItem>>, t: Throwable) {
                toast("${appTxt(R.string.network_error)}\n${t.localizedMessage}")
            }
        })
    }
}
