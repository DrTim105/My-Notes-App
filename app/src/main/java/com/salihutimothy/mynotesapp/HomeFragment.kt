package com.salihutimothy.mynotesapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.salihutimothy.mynotesapp.adapter.NotesAdapter
import com.salihutimothy.mynotesapp.database.NotesDatabase
import com.salihutimothy.mynotesapp.entities.Notes
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import android.graphics.Typeface
import android.util.TypedValue
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat


class HomeFragment : BaseFragment() {

    var arrNotes = ArrayList<Notes>()
    var notesAdapter: NotesAdapter = NotesAdapter()
    private lateinit var fabCreateNote: ExtendedFloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {

        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabCreateNote = view.findViewById(R.id.fabCreateNote) as ExtendedFloatingActionButton
        recyclerView = view.findViewById(R.id.rv_notes) as RecyclerView
        searchView = view.findViewById(R.id.search_view) as SearchView

//        searchView.clearFocus()
        val imm: InputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    fabCreateNote.extend()
                } else {
                    fabCreateNote.shrink()
                }
            }
        })

        launch {
            context?.let {
                val notes = NotesDatabase.getDatabase(it).notesDao().getAllNotes()
                notesAdapter.setData(notes)
                arrNotes = notes as ArrayList<Notes>
                recyclerView.adapter = notesAdapter
            }
        }

        notesAdapter.setOnClickListener(onClicked)

        fabCreateNote.setOnClickListener {
            replaceFragment(CreateNoteFragment.newInstance(), false)
        }

        val id =
            requireContext().resources!!.getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchView.findViewById(id) as TextView
        val myCustomFont = ResourcesCompat.getFont(requireContext(), R.font.lato)
        searchText.typeface = myCustomFont
        searchText.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimensionPixelSize(R.dimen.text_small).toFloat()
        )



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {

                val tempArr = ArrayList<Notes>()

                for (arr in arrNotes) {
                    if (arr.title!!.lowercase(Locale.getDefault()).contains(p0.toString()) ||
                        arr.noteText!!.lowercase(Locale.getDefault()).contains(p0.toString())
                    ) {
                        tempArr.add(arr)
                    }
                }

                notesAdapter.setData(tempArr)
                notesAdapter.notifyDataSetChanged()
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                val tempArr = ArrayList<Notes>()

                for (arr in arrNotes) {
                    if (arr.title!!.lowercase(Locale.getDefault()).contains(p0.toString()) ||
                        arr.noteText!!.lowercase(Locale.getDefault()).contains(p0.toString())
                    ) {
                        tempArr.add(arr)
                    }
                }

                notesAdapter.setData(tempArr)
                notesAdapter.notifyDataSetChanged()
                return true
            }

        })


    }

    private val onClicked = object : NotesAdapter.OnItemClickListener {
        override fun onClicked(noteId: Int) {

            val fragment: Fragment
            val bundle = Bundle()
            bundle.putInt("noteId", noteId)
            fragment = CreateNoteFragment.newInstance()
            fragment.arguments = bundle

            replaceFragment(fragment, false)

        }

    }

    private fun replaceFragment(fragment: Fragment, istransition: Boolean) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

//            fragmentTransition.setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out)
//            fragmentTransition.setCustomAnimations(R.anim.slide_right_to_left, R.anim.exit_right_to_left,
//            R.anim.slide_left_to_right, R.anim.exit_left_to_right)
        fragmentTransition.setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        fragmentTransition.replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }


}