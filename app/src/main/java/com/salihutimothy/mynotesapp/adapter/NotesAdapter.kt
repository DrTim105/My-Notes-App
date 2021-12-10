package com.salihutimothy.mynotesapp.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.salihutimothy.mynotesapp.R
import com.salihutimothy.mynotesapp.entities.Notes
//import kotlinx.android.synthetic.main.item_rv_notes.view.*


class NotesAdapter() :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    var listener:OnItemClickListener? = null
    var arrList = ArrayList<Notes>()
    private var rvDateTime: TextView? = null
    private var rvTitle: TextView? = null
    private var rvDesc: TextView? = null
    private var rvWeblink: TextView? = null
    private var cardView: CardView? = null
    private var imgNote : RoundedImageView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_notes,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return arrList.size
    }

    fun setData(arrNotesList: List<Notes>){
        arrList = arrNotesList as ArrayList<Notes>
    }

    fun setOnClickListener(listener1: OnItemClickListener){
        listener = listener1
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {

        rvTitle = holder.itemView.findViewById(R.id.tvTitle)
        rvDateTime = holder.itemView.findViewById(R.id.tvDateTime)
        rvDesc = holder.itemView.findViewById(R.id.tvDesc)
        rvWeblink = holder.itemView.findViewById(R.id.tvWebLink)
        cardView = holder.itemView.findViewById(R.id.cardView)
        imgNote = holder.itemView.findViewById(R.id.imgNote)

        rvTitle!!.text = arrList[position].title
        rvDesc!!.text = arrList[position].noteText
        rvDateTime!!.text = arrList[position].dateTime

        if (arrList[position].color != null){
            cardView!!.setCardBackgroundColor(Color.parseColor(arrList[position].color))
        }else{
            cardView!!.setCardBackgroundColor(Color.parseColor(R.color.cardDefault.toString()))
        }

        if (arrList[position].imgPath != null){
            imgNote!!.setImageBitmap(BitmapFactory.decodeFile(arrList[position].imgPath))
            imgNote!!.visibility = View.VISIBLE
        }else{
            imgNote!!.visibility = View.GONE
        }

        if (arrList[position].webLink != ""){
            rvWeblink!!.text = arrList[position].webLink
            rvWeblink!!.visibility = View.VISIBLE
        }else{
            rvWeblink!!.visibility = View.GONE
        }

        cardView!!.setOnClickListener {
            listener!!.onClicked(arrList[position].id!!)
        }

    }

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }


    interface OnItemClickListener{
        fun onClicked(noteId:Int)
    }

}