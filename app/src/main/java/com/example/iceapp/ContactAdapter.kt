package com.example.iceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// This simple object holds the two pieces of information together
data class Contact(val designation: String, val number: String)

class ContactAdapter(
    private val contacts: MutableList<Contact>,
    private val onDeleteClicked: (Int) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDesignation: TextView = view.findViewById(R.id.tvDesignation)
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.tvDesignation.text = contact.designation
        holder.tvNumber.text = contact.number

        holder.btnDelete.setOnClickListener {
            onDeleteClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }
}