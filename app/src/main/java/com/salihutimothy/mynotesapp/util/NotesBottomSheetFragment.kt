package com.salihutimothy.mynotesapp.util

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salihutimothy.mynotesapp.R

class NotesBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var fNote1: FrameLayout
    private lateinit var fNote2: FrameLayout
    private lateinit var fNote3: FrameLayout
    private lateinit var fNote4: FrameLayout
    private lateinit var fNote5: FrameLayout
    private lateinit var fNote6: FrameLayout

    private lateinit var imgNote1: ImageView
    private lateinit var imgNote2: ImageView
    private lateinit var imgNote3: ImageView
    private lateinit var imgNote4: ImageView
    private lateinit var imgNote5: ImageView
    private lateinit var imgNote6: ImageView
    private lateinit var imgMore: ImageView

    private lateinit var layoutImage: LinearLayout
    private lateinit var layoutWebUrl: LinearLayout
    private lateinit var layoutDeleteNote: LinearLayout

    var selectedColor = "#171C26" // default color of notes

    companion object {

        var noteId = -1
        fun newInstance(id: Int) =
            NotesBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    noteId = id
                }
            }
    }


    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val view = LayoutInflater.from(context).inflate(R.layout.fragment_bottom_sheet, null)
        dialog.setContentView(view)

        val param = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams

        val behavior = param.behavior

        if (behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    var state = ""
                    when (newState) {
                        BottomSheetBehavior.STATE_DRAGGING -> {
                            state = "DRAGGING"
                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                            state = "SETTLING"
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            state = "EXPANDED"
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            state = "COLLAPSED"
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            state = "HIDDEN"
                            dismiss()
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                            TODO()
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutDeleteNote = requireDialog().findViewById(R.id.layoutDeleteNote) as LinearLayout

        if (noteId != -1) {
            layoutDeleteNote.visibility = View.VISIBLE
        } else {
            layoutDeleteNote.visibility = View.GONE
        }
        setListener()
    }

    private fun setListener() {
        Log.d("NOTES_BOTTOM_FRAGMENT", "setListener clicked")

        fNote1 = requireDialog().findViewById(R.id.fNote1) as FrameLayout
        fNote2 = requireDialog().findViewById(R.id.fNote2) as FrameLayout
        fNote3 = requireDialog().findViewById(R.id.fNote3) as FrameLayout
        fNote4 = requireDialog().findViewById(R.id.fNote4) as FrameLayout
        fNote5 = requireDialog().findViewById(R.id.fNote5) as FrameLayout
        fNote6 = requireDialog().findViewById(R.id.fNote6) as FrameLayout

        imgNote1 = requireDialog().findViewById(R.id.imgNote1) as ImageView
        imgNote2 = requireDialog().findViewById(R.id.imgNote2) as ImageView
        imgNote3 = requireDialog().findViewById(R.id.imgNote3) as ImageView
        imgNote4 = requireDialog().findViewById(R.id.imgNote4) as ImageView
        imgNote5 = requireDialog().findViewById(R.id.imgNote5) as ImageView
        imgNote6 = requireDialog().findViewById(R.id.imgNote6) as ImageView
        imgMore = requireDialog().findViewById(R.id.imgMore) as ImageView

        layoutImage = requireDialog().findViewById(R.id.layoutImage) as LinearLayout
        layoutWebUrl = requireDialog().findViewById(R.id.layoutWebUrl) as LinearLayout
        layoutDeleteNote = requireDialog().findViewById(R.id.layoutDeleteNote) as LinearLayout


        fNote1.setOnClickListener {
            Log.d("NOTES_BOTTOM_FRAGMENT", "fNote1 clicked")

            imgNote1.setImageResource(R.drawable.ic_tick) // tick image
            imgNote2.setImageResource(0)
            imgNote3.setImageResource(0)
            imgNote4.setImageResource(0)
            imgNote5.setImageResource(0)
            imgNote6.setImageResource(0)
            selectedColor = "#DBFEF8"

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Blue")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)

        }

        fNote2.setOnClickListener {
            imgNote1.setImageResource(0)
            imgNote2.setImageResource(R.drawable.ic_tick)
            imgNote3.setImageResource(0)
            imgNote4.setImageResource(0)
            imgNote5.setImageResource(0)
            imgNote6.setImageResource(0)
            selectedColor = "#D4B996"

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Brown")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)

        }

        fNote3.setOnClickListener {
            imgNote1.setImageResource(0)
            imgNote2.setImageResource(0)
            imgNote3.setImageResource(R.drawable.ic_tick)
            imgNote4.setImageResource(0)
            imgNote5.setImageResource(0)
            imgNote6.setImageResource(0)
            selectedColor = "#DFFFA5"

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Green")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)

        }

        fNote4.setOnClickListener {
            imgNote1.setImageResource(0)
            imgNote2.setImageResource(0)
            imgNote3.setImageResource(0)
            imgNote4.setImageResource(R.drawable.ic_tick)
            imgNote5.setImageResource(0)
            imgNote6.setImageResource(0)
            selectedColor = "#FCF6B1"

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Yellow")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

        fNote5.setOnClickListener {

            imgNote1.setImageResource(0)
            imgNote2.setImageResource(0)
            imgNote3.setImageResource(0)
            imgNote4.setImageResource(0)
            imgNote5.setImageResource(R.drawable.ic_tick)
            imgNote6.setImageResource(0)
            selectedColor = "#FFCCCC"

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Pink")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

        fNote6.setOnClickListener {
            imgNote1.setImageResource(0)
            imgNote2.setImageResource(0)
            imgNote3.setImageResource(0)
            imgNote4.setImageResource(0)
            imgNote5.setImageResource(0)
            imgNote6.setImageResource(R.drawable.ic_tick)
            selectedColor = "#F0F0F0"

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Grey")
            intent.putExtra("selectedColor", selectedColor)
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
        }

        layoutImage.setOnClickListener {
            Log.d("NOTES_BOTTOM_FRAGMENT", "layoutImage clicked")

            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "Image")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }
        layoutWebUrl.setOnClickListener {
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "WebUrl")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }
        layoutDeleteNote.setOnClickListener {
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action", "DeleteNote")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }
    }
}