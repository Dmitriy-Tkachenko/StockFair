package com.tkachenko.stockfair.view.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tkachenko.stockfair.R
import com.tkachenko.stockfair.model.entities.Stocks
import com.tkachenko.stockfair.utils.Utils
import okio.Utf8

interface Delegate {
    fun addStock(symbol: String, name: String, currentPrice: Double, changePrice: Double, changePercent: Double)
}

class StockFairAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mStockFairData: MutableList<Stocks.StockItem?> = mutableListOf()
    private var delegate: Delegate? = null

    fun attachDelegate(delegate: Delegate) {
        this.delegate = delegate
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newStockItem: List<Stocks.StockItem>) {
        mStockFairData.clear()
        mStockFairData.addAll(newStockItem)
        notifyDataSetChanged()
    }

    fun updateStar(symbol: String) {
        for (index in mStockFairData.indices) {
            if (mStockFairData[index]?.symbol == symbol.uppercase()) {
                mStockFairData[index]?.favouriteStock = true
                notifyItemChanged(index, Utils.STAR)
            }
        }
    }

    fun insertData(newStockItem: List<Stocks.StockItem>) {
        mStockFairData.addAll(newStockItem)
        Log.i("SetData", mStockFairData.size.toString())
        notifyItemRangeInserted(mStockFairData.size - 1, newStockItem.size)
    }

    fun updatePriceAndPercent(currentPrice: Double, changePrice: Double, changePercent: Double, symbol: String) {
        for (index in mStockFairData.indices) {
            if (mStockFairData[index]?.symbol == symbol.uppercase()) {
                mStockFairData[index]?.currentPrice = currentPrice
                mStockFairData[index]?.changePrice = changePrice
                mStockFairData[index]?.changePercent = changePercent
                notifyItemChanged(index, Utils.PRICE_AND_PERCENT)
            }
        }
    }

    fun getPreviousClose(symbol: String): Double? {
        var previousClosePrice: Double? = null
        for (index in mStockFairData.indices) {
            if (mStockFairData[index]?.symbol == symbol.uppercase()) {
                previousClosePrice = mStockFairData[index]?.previousClosePrice
            }
        }
        return previousClosePrice
    }

    fun addNull() {
        mStockFairData.add(null)
        notifyItemInserted(mStockFairData.size - 1)
    }

    fun removeNull() {
        mStockFairData.removeAt(mStockFairData.size - 1)
        notifyItemRemoved(mStockFairData.size)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> StockViewHolder(itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.stock_fair_item, viewGroup, false), delegate = delegate)
            0 -> LoadingViewHolder(itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_loading, viewGroup, false))
            else -> StockViewHolder(itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.stock_fair_item, viewGroup, false), delegate = delegate)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StockViewHolder -> mStockFairData[position]?.let {
                holder.bind(model = it)
                holder.chooseColorViewHolder(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            for (payload in payloads) {
                if (payload == Utils.PRICE_AND_PERCENT) {
                    StockViewHolder(holder.itemView, delegate).bindPriceAndPercent(mStockFairData[position]?.currentPrice, mStockFairData[position]?.changePrice, mStockFairData[position]?.changePercent)
                }
                if (payload == Utils.STAR) {
                    StockViewHolder(holder.itemView, delegate).colorStar()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mStockFairData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mStockFairData[position] != null) {
            Utils.VIEW_TYPE_STOCK
        } else {
            Utils.VIEW_TYPE_LOADING
        }
    }

    class StockViewHolder(itemView: View, val delegate: Delegate?): RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.card_view)
        private val stockNameImageView: ImageView = itemView.findViewById(R.id.imageView)
        private val stockNameTextView: TextView = itemView.findViewById(R.id.stock_name)
        private val stockTickerTextView: TextView = itemView.findViewById(R.id.stock_ticker)
        private val stockPriceTextView: TextView = itemView.findViewById(R.id.stock_price)
        private val stockStarImageView: ImageView = itemView.findViewById(R.id.image_star)
        private val stockPriceChangeTextView: TextView = itemView.findViewById(R.id.stock_price_change)

        @SuppressLint("SetTextI18n")
        fun bind(model: Stocks.StockItem) {
            Picasso.get().load(Utils.loadImage(model.symbol)).into(stockNameImageView)
            stockNameTextView.text = model.name
            stockTickerTextView.text = model.symbol
            stockPriceTextView.text = "$${model.currentPrice}"

            if (model.changePrice >= 0) {
                stockPriceChangeTextView.text = "+${model.changePrice} $ (+${model.changePercent}%)"
                stockPriceChangeTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
            } else {
                stockPriceChangeTextView.text = "${model.changePrice} $ (${model.changePercent}%)"
                stockPriceChangeTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            }

            stockStarImageView.setOnClickListener {
                delegate?.addStock(model.symbol, model.name, model.currentPrice, model.changePrice, model.changePercent)
            }
        }

        fun chooseColorViewHolder(position: Int) {
            if (position % 2 != 0) {
                cardView.backgroundTintMode = PorterDuff.Mode.CLEAR
            } else {
                cardView.backgroundTintMode = PorterDuff.Mode.DARKEN
            }
        }

        fun colorStar() {
            stockStarImageView.setColorFilter(Color.YELLOW)
        }

        fun bindPriceAndPercent(currentPrice: Double?, changePrice: Double?, changePercent: Double?) {
            stockPriceTextView.text = currentPrice.toString()

            if (changePrice != null && changePrice >= 0) {
                stockPriceChangeTextView.text = "+$changePrice $ (+$changePercent%)"
                stockPriceChangeTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
            } else {
                stockPriceChangeTextView.text = "$changePrice $ ($changePercent%)"
                stockPriceChangeTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            }
        }
    }

    class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }
}