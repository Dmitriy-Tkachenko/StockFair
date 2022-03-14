package com.tkachenko.stockfair.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tkachenko.stockfair.R
import com.tkachenko.stockfair.model.entities.FavouriteStock
import com.tkachenko.stockfair.model.entities.Stocks
import com.tkachenko.stockfair.view.adapters.Delegate
import com.tkachenko.stockfair.view.adapters.StockFairAdapter
import com.tkachenko.stockfair.viewmodel.StockFairViewModel
import kotlin.math.roundToInt

class StockFairFragment : Fragment() {
    private lateinit var stockFairRecyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val mAdapter = StockFairAdapter()
    private var updateStocksIsCalled = false

    private val stockFairViewModel: StockFairViewModel by lazy {
        ViewModelProvider(this)[StockFairViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAdapter.attachDelegate(object: Delegate{
            override fun addStock(symbol: String, name: String, currentPrice: Double, changePrice: Double, changePercent: Double) {
                addFavouriteStock(symbol, name, currentPrice, changePrice, changePercent)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stock_fair_list, container, false)
        stockFairRecyclerView = view.findViewById(R.id.stock_fair_recycler_view)
        linearLayoutManager = LinearLayoutManager(context)
        stockFairRecyclerView.layoutManager = linearLayoutManager
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadStocks(posFirst = 0, posLast = 0)
    }

    private fun loadStocks(posFirst: Int, posLast: Int) {
        stockFairViewModel.fetchPopularSymbols().observe(
            viewLifecycleOwner,
            { symbolItems ->
                val symbols: MutableList<String> = mutableListOf()
                for (item in symbolItems.subList(posFirst, posLast + 1)) {
                    symbols.add(item.symbol)
                }
                val popularStocksSize = symbolItems.size
                if (mAdapter.itemCount == 0) {
                    setStocks(symbols)
                } else {
                    insertStocks(symbols)
                }
                onScrollListener(popularStocksSize)
            }
        )
    }

    private fun setStocks(symbols: List<String>) {
        val symbolsString = symbols.joinToString(",")
        stockFairViewModel.fetchStartStocks(symbols = symbolsString).observe(
            viewLifecycleOwner,
            { stockItems ->
                stockFairRecyclerView.adapter = mAdapter
                mAdapter.setData(stockItems)
                stockFairViewModel.fetchStartStocksWebSocket(symbols).observe(
                    viewLifecycleOwner,
                    { stockItem ->
                        updatePriceAndPercent(stockItem)
                    }
                )
            }
        )
    }

    private fun insertStocks(symbols: List<String>) {
        val symbolsString = symbols.joinToString(",")
        stockFairViewModel.fetchStocks(symbolsString).observe(
            viewLifecycleOwner,
            { stockItems ->
                mAdapter.removeNull()
                mAdapter.insertData(stockItems)
                updateStocksIsCalled = false

                stockFairViewModel.fetchStocksWebSocket(symbols).observe(
                    viewLifecycleOwner,
                    { stockItem ->
                        updatePriceAndPercent(stockItem)
                    }
                )
            }
        )
    }

    private fun updatePriceAndPercent(stockItem: String) {

        val currentPrice = getCurrentPriceFromJsonWebSocket(stockItem)
        if (currentPrice != null) {
            val currentSymbol = getCurrentSymbolFromJsonWebSocket(stockItem)
            val previousClosePrice = currentSymbol.let { mAdapter.getPreviousClose(symbol = it) }

            val changePrice = previousClosePrice?.let {
                stockFairViewModel.calcChangePrice(currentPrice = currentPrice.toDouble(), previousClosePrice = it)
                roundToTwoDecimal(it)
            }
            val changePercent = previousClosePrice?.let {
                stockFairViewModel.calcChangePercent(currentPrice = currentPrice.toDouble(), previousClosePrice = it)
                roundToTwoDecimal(it)
            }

            if (changePrice != null && changePercent != null) {
                mAdapter.updatePriceAndPercent(currentPrice = currentPrice.toDouble(), changePrice = changePrice, changePercent = changePercent, currentSymbol)
            }
        }
    }

    private fun getCurrentPriceFromJsonWebSocket(stockItem: String): Double? {
        return if (!stockItem.contains("\"event\":\"login\"") && !stockItem.contains("\"event\":\"subscribe\"") && !stockItem.contains("\"event\":\"unsubscribe\"")) {
            val indexStartPrice = stockItem.indexOf("\"lp\":") + 5
            val indexEndPrice = stockItem.indexOf(",\"ls\"")
            val currentPrice = stockItem.substring(indexStartPrice, indexEndPrice)

            if (currentPrice != "null") {
                currentPrice.toDouble()
            } else {
                null
            }
        } else {
            null
        }
    }

    private fun getCurrentSymbolFromJsonWebSocket(stockItem: String): String {
        val indexStartSymbol = stockItem.indexOf("\"s\":\"") + 5
        val indexEndSymbol = stockItem.indexOf("\",\"t\"")
        return stockItem.substring(indexStartSymbol, indexEndSymbol)
    }

    private fun roundToTwoDecimal(value: Double) = (value * 100).roundToInt() / 100.0

    private fun addFavouriteStock(symbol: String, name: String, currentPrice: Double, changePrice: Double, changePercent: Double) {
        stockFairViewModel.addFavouriteStock(FavouriteStock(symbol, name, currentPrice, changePrice, changePercent))
    }

    private fun onScrollListener(popularStocksSize: Int) {
        stockFairRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                // Устанавливаем длину буффера - количество акций, которые будем подгружать при скролле
                val bufferLength = 20
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                if (totalItemCount == linearLayoutManager.findLastVisibleItemPosition() + 1) {
                    if (!updateStocksIsCalled) {
                        when {
                            totalItemCount == popularStocksSize -> {
                                return
                            }
                            totalItemCount + bufferLength <= popularStocksSize -> {
                                mAdapter.addNull()
                                updateStocksIsCalled = true
                                loadStocks(posFirst = totalItemCount, posLast = totalItemCount + bufferLength - 1)
                            }
                            totalItemCount + bufferLength > popularStocksSize -> {
                                mAdapter.addNull()
                                updateStocksIsCalled = true
                                loadStocks(posFirst = totalItemCount, posLast = popularStocksSize - 1)
                            }
                        }
                    }
                }
            }
        })
    }
}