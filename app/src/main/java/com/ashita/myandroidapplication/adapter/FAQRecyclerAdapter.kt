package com.ashita.myandroidapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.model.FoodFaq

class FAQRecyclerAdapter(val context: Context, val faqList: ArrayList<FoodFaq>): RecyclerView.Adapter<FAQRecyclerAdapter.FAQViewHolder>() {

    class FAQViewHolder(view: View): RecyclerView.ViewHolder(view){

        val txtQuestion: TextView = view.findViewById(R.id.txtQuestion)
        val txtAnswer: TextView = view.findViewById(R.id.txtAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_faq_single_row, parent, false)

        return FAQViewHolder(view)
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faq = faqList[position]

        holder.txtQuestion.text = faq.question
        holder.txtAnswer.text = faq.answer
    }
}