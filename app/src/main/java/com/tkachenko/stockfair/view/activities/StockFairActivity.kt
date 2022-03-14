package com.tkachenko.stockfair.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tkachenko.stockfair.R
import com.tkachenko.stockfair.model.database.FavouriteStockRepository
import com.tkachenko.stockfair.utils.Utils
import com.tkachenko.stockfair.view.fragments.FavouriteStocksFragment
import com.tkachenko.stockfair.view.fragments.StockFairFragment

class StockFairActivity : AppCompatActivity() {
    private lateinit var adapter: FragmentStateAdapter
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_fair)

        FavouriteStockRepository.initialize(applicationContext)

        viewPager2 = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        adapter = StockFairAdapter(this)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = Utils.namesTabs[position]
        }.attach()

        changeTextSizeInTab(tabLayout.getTabAt(1), 18F)
        selectedBetweenTab()
    }

    // Resize text when swiping between tabs
    private fun selectedBetweenTab() {
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                changeTextSizeInTab(tab, 28F)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                changeTextSizeInTab(tab, 18F)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    // Resize text in Tab 2 when start Fragment
    private fun changeTextSizeInTab(tab: TabLayout.Tab?, textSize: Float) {
        val vg: ViewGroup = tabLayout.getChildAt(0) as ViewGroup
        val vgTab: ViewGroup = tab?.let { vg.getChildAt(it.position) } as ViewGroup
        val tabChildCount = vgTab.childCount
        for (i in 0 until tabChildCount) {
            val tabViewChild = vgTab.getChildAt(i)
            if (tabViewChild is TextView) {
                tabViewChild.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            }
        }
    }

    inner class StockFairAdapter(appCompatActivity: AppCompatActivity): FragmentStateAdapter(appCompatActivity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return StockFairFragment()
                1 -> return FavouriteStocksFragment()
            }
            return StockFairFragment()
        }
    }
}