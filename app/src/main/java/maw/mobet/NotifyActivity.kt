package maw.mobet

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_notify.*
import kotlinx.android.synthetic.main.custom_actionbar.*
import kotlinx.android.synthetic.main.list_item_notify.*
import maw.mobet.api.IdData
import maw.mobet.api.NotifyItem
import maw.mobet.api.ResultItem
import maw.mobet.notify.MyAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import splitties.activities.start
import splitties.resources.txt
import splitties.toast.toast

class NotifyActivity : AppCompatActivity(), MyAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private val list = MutableLiveData<List<NotifyItem>>().apply {
        loadData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notify)

        // 액션바
        back_img.visibility = View.VISIBLE
        noti_img.visibility = View.GONE
        back_img.setOnClickListener {
            finish()
        }

        list_view.layoutManager = LinearLayoutManager(this)
        list.observe(this, Observer {
            list_view.adapter = MyAdapter(it.toMutableList(), this)
            swipe_l.isRefreshing = false
        })

        swipe_l.setOnRefreshListener(this)
    }

    private fun loadData() {
        val service = RetrofitClient.getInstance()
        val dataCall = service.notifyList()
        dataCall.enqueue(object : Callback<List<NotifyItem>> {
            override fun onResponse(
                call: Call<List<NotifyItem>>, response: Response<List<NotifyItem>>
            ) {
                list.value = response.body() ?: emptyList()
            }

            override fun onFailure(call: Call<List<NotifyItem>>, t: Throwable) {
                toast("${txt(R.string.network_error)}\n${t.localizedMessage}")
            }
        })
    }

    // 리스트 아이템 클릭
    override fun onItemClick(view: View, position: Int, delete: () -> Unit) {
        view.isClickable = false

        val item = view.tag as NotifyItem
        val type = if (view.id == accept_btn.id) 1 else 0

        val service = RetrofitClient.getInstance()
        val dataCall = service.notifyRequest(IdData(item.id, type))
        dataCall.enqueue(object : Callback<ResultItem> {
            override fun onResponse(call: Call<ResultItem>, response: Response<ResultItem>) {
                val result = response.body()
                when (result?.code) {
                    // 사용가능
                    0 -> {
                        delete()
                        if (type == 1) {
                            start<GameActivity> {
                                putExtra("id", item.gameId)
                            }
                        }
                        view.isClickable = true
                    }
                    // 오류
                    else -> {
                        toast("${txt(R.string.error)} ${result?.code}")
                        view.isClickable = true
                    }
                }
            }

            override fun onFailure(call: Call<ResultItem>, t: Throwable) {
                toast("${txt(R.string.network_error)}\n${t.localizedMessage}")
                view.isClickable = true
            }
        })
    }

    // 당겨서 새로고침
    override fun onRefresh() {
        loadData()
    }
}
