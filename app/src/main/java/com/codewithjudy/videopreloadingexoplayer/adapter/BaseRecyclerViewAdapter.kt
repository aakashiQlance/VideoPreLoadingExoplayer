package com.codewithjudy.videopreloadingexoplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import java.util.*


abstract class BaseRecyclerViewAdapter<T>(val mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mArrayList: MutableList<T>? = arrayListOf()
    private var lastPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getView(), parent, false)
        return getViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (mArrayList != null && mArrayList!!.size > 0) {
            val obj = mArrayList!![holder.adapterPosition]
            setData(holder, obj, position)
        }
    }


    override fun getItemCount(): Int {
        return mArrayList!!.size
    }

    abstract fun getViewHolder(view: View): RecyclerView.ViewHolder

    abstract fun getView(): Int

    abstract fun setData(holder: RecyclerView.ViewHolder, data: T, position: Int)

    fun addAll(mArrayList: MutableList<T>?) {
        if (null != mArrayList) {
            this.mArrayList!!.addAll(mArrayList)
            notifyItemRangeInserted(itemCount, mArrayList.size)
        }
    }

    fun updateAll(mArrayLists: MutableList<T>) {
        if (!mArrayLists.isNullOrEmpty()){
            this.mArrayList?.clear()
            this.mArrayList!!.addAll(mArrayLists)
            notifyDataSetChanged()
        }
    }

    fun clear() {
        this.mArrayList!!.clear()
        notifyDataSetChanged()
    }

    fun update(position: Int, obj: T) {
        this.mArrayList!!.set(position, obj)
        notifyItemChanged(position)
    }

    fun remove(position: Int) {
        this.mArrayList!!.removeAt(position)
        notifyItemRemoved(position)
      //  notifyItemRangeChanged(position, mArrayList!!.size)
    }

    fun removeItem(position: Int) {
        this.mArrayList!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mArrayList!!.size)
    }

    fun add(obj: T) {
        val position = mArrayList!!.size
        this.mArrayList!!.add(obj)
        notifyItemInserted(position)
    }

    fun add(position: Int, obj: T) {
        this.mArrayList!!.add(obj)
        notifyItemInserted(position)
    }

    fun addPosition(position: Int, obj: T) {

        this.mArrayList!!.add(position, obj)
        notifyItemInserted(position)
    }

    fun getData(): MutableList<T>? {
        return mArrayList
    }

    fun setAnimation(context: Context, viewToAnimate: View, position: Int, animationType: Int) {
        if (position > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(context, animationType)
            animation.duration = Random().nextInt(501).toLong()
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}