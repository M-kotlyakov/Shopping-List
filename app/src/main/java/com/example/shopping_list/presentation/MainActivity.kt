package com.example.shopping_list.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shopping_list.R
import com.example.shopping_list.presentation.ShopItemActivity.Companion.newIntentAddItem
import com.example.shopping_list.presentation.ShopItemActivity.Companion.newIntentEditItem
import com.example.shopping_list.presentation.ShopListAdapter.Companion.MAX_POOL_SIZE
import com.example.shopping_list.presentation.ShopListAdapter.Companion.VIEW_TYPE_DISABLED
import com.example.shopping_list.presentation.ShopListAdapter.Companion.VIEW_TYPE_ENABLED
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var shopListAdapter: ShopListAdapter
    private var shopItemContainer : FragmentContainerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()

        shopItemContainer = findViewById(R.id.shop_item_container)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.shopList.observe(this) {
            shopListAdapter.submitList(it)
        }
        val buttonAdd = findViewById<FloatingActionButton>(R.id.button_add_shop_item)
        buttonAdd.setOnClickListener {
            if (isOnePaneMode()) {
                val intent = newIntentAddItem(this)
                startActivity(intent)
            } else {
                launchFragment(ShopItemFragment.newInstanceAddItem())
            }
        }
    }

    override fun onEditingFinished() {
        Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
    }

    private fun isOnePaneMode(): Boolean {
        return shopItemContainer == null
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.shop_item_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupRecyclerView() {
        val rvShopList = findViewById<RecyclerView>(R.id.rv_shop_list)
        shopListAdapter = ShopListAdapter()
        with(rvShopList){
            adapter = shopListAdapter
            recycledViewPool.setMaxRecycledViews(VIEW_TYPE_ENABLED, MAX_POOL_SIZE)
            recycledViewPool.setMaxRecycledViews(VIEW_TYPE_DISABLED, MAX_POOL_SIZE)
        }
        setupLongClickListener()
        setupClickListener()
        setupSwipeListener(rvShopList)
    }

    private fun setupSwipeListener(rvShopList: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = shopListAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteShopItem(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }

    private fun setupClickListener() {
        shopListAdapter.onShopItemClickListener = {
            if (isOnePaneMode()) {
                val intent = newIntentEditItem(this, it.id)
                startActivity(intent)
            } else {
                launchFragment(ShopItemFragment.newInstanceEditItem(it.id))
            }
        }
    }

    private fun setupLongClickListener() {
        shopListAdapter.onShopItemLongClickListener = {
            viewModel.changeEnabledState(it)
        }
    }
}