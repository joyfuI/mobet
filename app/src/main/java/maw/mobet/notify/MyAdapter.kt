package maw.mobet.notify

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.list_item_notify.view.*
import maw.mobet.R
import maw.mobet.api.NotifyListItem
import maw.mobet.dpToPx
import splitties.resources.appColorSL

class MyAdapter(
    private val data: List<NotifyListItem>, private val listener: OnClickListener? = null
) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onClick(view: View)
    }

    private val onClickListener = View.OnClickListener {
        listener?.onClick(it)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_notify, parent, false)
        var shape: GradientDrawable

        shape = view.accept_btn.background as GradientDrawable
        shape.color = appColorSL(R.color.colorDeposit)
        shape = view.reject_btn.background as GradientDrawable
        shape.color = appColorSL(R.color.colorWithdrawal)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        Glide.with(holder.itemView).load(item.imgUrl)
            .apply(RequestOptions().circleCrop())
            .override(dpToPx(45.5f).toInt())
            .into(holder.profileImg)
        holder.profileTxt.text = item.name
        holder.msgTxt.text = item.msg

        with (holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImg: ImageView = itemView.profile_img
        val profileTxt: TextView = itemView.profile_txt
        val msgTxt: TextView = itemView.msg_txt
    }
}
