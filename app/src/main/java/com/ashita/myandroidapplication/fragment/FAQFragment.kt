package com.ashita.myandroidapplication.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashita.myandroidapplication.R
import com.ashita.myandroidapplication.adapter.FAQRecyclerAdapter
import com.ashita.myandroidapplication.model.FoodFaq

class FAQFragment : Fragment() {

    lateinit var recyclerFAQFragment: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FAQRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var faqList = arrayListOf<FoodFaq>(
        FoodFaq("Q1. What is Foodure?", "A1. Foodure is a comprehensive online food ordering system that helps your customers to order food online. It's an easy to use platform that connects your food business with your potential customers."),
        FoodFaq("Q2. Will I get any support after my purchase?", "A2. We are happy to provide free technical support to our valuable customers. Under the Bite plan, customers will get the support for the first 3 months and in Buffet plan customers will get support for the first 6 months."),
        FoodFaq("Q3. Is Foodure for restaurant owners or delivery services?", "A3. Foodure is an online food ordering and delivery software that helps restaurant owners as well as delivery services to delivery food online."),
        FoodFaq("Q4. Are there any hidden charges?", "A4. No, our pricing and quotes are transparent."),
        FoodFaq("Q5. How do I request for refund?", "A5. As per our Refund Policy , refund requests are not entertained at this time. We would request you to go through the demo of our product before you make a purchase decision.")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_faq, container, false)

        recyclerFAQFragment = view.findViewById(R.id.recyclerFAQ)

        layoutManager = LinearLayoutManager(activity)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        recyclerAdapter = FAQRecyclerAdapter(activity as Context, faqList)

        progressLayout.visibility = View.GONE

        recyclerFAQFragment.adapter = recyclerAdapter

        recyclerFAQFragment.layoutManager = layoutManager

        return view
    }

}
