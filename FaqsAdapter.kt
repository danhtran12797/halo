package com.oneibc.feature.jurisdiction.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oneibc.common.extension.wvSetContent
import com.oneibc.common.utils.Constants
import com.oneibc.databinding.ItemFaqsBinding
import com.oneibc.feature.jurisdiction.model_temp.FaqData
import java.util.*

class FaqsAdapter(private val listFaq: MutableList<FaqData> = mutableListOf()) :
    RVAdapter<FaqData>(listFaq) {

    var callBackItemClick: (Int) -> Unit = {}

    fun addAll(list: List<FaqData>) {
        origValues = list.toMutableList()
        resultList = list
    }

    private val diffCallback = object : DiffUtil.ItemCallback<FaqData>() {
        override fun areItemsTheSame(
            oldItem: FaqData,
            newItem: FaqData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FaqData,
            newItem: FaqData
        ): Boolean {
            return newItem == oldItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    init {
        listFaq.also { resultList = it }
    }

    var resultList: List<FaqData>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FaqsViewHolder(
            ItemFaqsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FaqsViewHolder).bind(resultList[position])
    }

    override fun searchCriteria(searchText: String, value: FaqData): Boolean {
        return value.name.toLowerCase(Locale.ROOT)
            .contains(searchText.toLowerCase(Locale.ROOT).trim())
    }

    inner class FaqsViewHolder(private val binding: ItemFaqsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

        }

        fun bind(item: FaqData) {
            binding.txtTitle.text = item.name
//            binding.frameLayout.isVisible = item.isExpand
            binding.contentWebView.wvSetContent(
                item.content,
                Constants.txt_header2,
                Constants.txt_footer
            )
            binding.contentWebView.isVisible = item.isExpand
            binding.imgAdd.setOnClickListener {
                val listTemp = resultList.toMutableList()
                listTemp[adapterPosition] = listTemp[adapterPosition].copy(isExpand = true)
                resultList=listTemp
            }
        }
    }

    override var callback: (MutableList<FaqData>) -> Unit = {
        resultList = it
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

}