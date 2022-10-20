package com.example.shopping_list.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.shopping_list.R
import com.example.shopping_list.domain.ShopItem

class ShopListAdapter : ListAdapter<ShopItem, ShopItemViewHolder>(ShopItemDiffCallback()) {

    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null
    var onShopItemClickListener: ((ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {

        val layout = when(viewType) {
            VIEW_TYPE_ENABLED -> R.layout.item_shop_enabled
            VIEW_TYPE_DISABLED -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Unknown view type $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ShopItemViewHolder, position: Int) {
        val item = getItem(position)
        viewHolder.itemView.setOnLongClickListener {
            onShopItemLongClickListener?.invoke(item)
            true
        }
        viewHolder.itemView.setOnClickListener {
            onShopItemClickListener?.invoke(item)
        }
        viewHolder.tvName.text = item.name
        viewHolder.tvCount.text = item.count.toString()
    }

    override fun getItemViewType(position: Int): Int {

        val item = getItem(position)
        return if (item.enabled) {
            VIEW_TYPE_ENABLED
        } else {
            VIEW_TYPE_DISABLED
        }
    }

    companion object {

        const val VIEW_TYPE_ENABLED = 1
        const val VIEW_TYPE_DISABLED = 0

        const val MAX_POOL_SIZE = 15
    }
}