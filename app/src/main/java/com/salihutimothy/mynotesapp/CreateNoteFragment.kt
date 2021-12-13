package com.salihutimothy.mynotesapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.salihutimothy.mynotesapp.database.NotesDatabase
import com.salihutimothy.mynotesapp.entities.Notes
import com.salihutimothy.mynotesapp.util.NotesBottomSheetFragment
import kotlinx.coroutines.launch
import java.text.DateFormat.getDateTimeInstance
import java.util.*


class CreateNoteFragment : BaseFragment() {


    private lateinit var tvDateTime: TextView
    private lateinit var tvWebLink: TextView
    private lateinit var imgDone: ImageView
    private lateinit var imgBack: ImageView
    private lateinit var imgMore: ImageView
    private lateinit var imgNote: ImageView
    private lateinit var imgDelete: ImageView
    private lateinit var imgUrlDelete: ImageView
    private lateinit var etNoteTitle: EditText
    private lateinit var etSubTitle: EditText
    private lateinit var etNoteDesc: EditText
    private lateinit var etWebLink: EditText
    private lateinit var colorView: View
    private lateinit var layoutImage: RelativeLayout
    private lateinit var layoutWebUrl: LinearLayout
    private lateinit var layoutNote: ConstraintLayout
    private lateinit var btnOk: Button
    private lateinit var btnCancel: Button
    var currentDate: String? = null

    private var selectedImagePath = ""
    private var webLink = ""
    private var noteId = -1

    var selectedColor = "#F0F0F0" //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = requireArguments().getInt("noteId", -1)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_note, container, false)
    }

    companion object {
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDateTime = view.findViewById(R.id.tv_date_time) as TextView
        tvWebLink = view.findViewById(R.id.tvWebLink) as TextView
        imgDone = view.findViewById(R.id.done) as ImageView
        imgBack = view.findViewById(R.id.back) as ImageView
        imgMore = view.findViewById(R.id.imgMore) as ImageView
        imgNote = view.findViewById(R.id.imgCreateNote) as ImageView
        imgDelete = view.findViewById(R.id.imgDelete) as ImageView
        imgUrlDelete = view.findViewById(R.id.imgUrlDelete) as ImageView
        etWebLink = view.findViewById(R.id.etWebLink) as EditText
        etNoteTitle = view.findViewById(R.id.et_note_title) as EditText
        etSubTitle = view.findViewById(R.id.et_note_subtitle) as EditText
        etNoteDesc = view.findViewById(R.id.et_note_desc) as EditText
        colorView = view.findViewById(R.id.colorView) as View
        btnOk = view.findViewById(R.id.btnOk) as Button
        btnCancel = view.findViewById(R.id.btnCancel) as Button
        layoutImage = view.findViewById(R.id.layoutImage) as RelativeLayout
        layoutWebUrl = view.findViewById(R.id.layoutWebUrl) as LinearLayout

        if (noteId != -1) {
            launch {
                context?.let {
                    val notes = NotesDatabase.getDatabase(it).notesDao().getSpecificNote(noteId)
                    colorView.setBackgroundColor(Color.parseColor(notes.color))
                    etNoteTitle.setText(notes.title)
                    etSubTitle.setText(notes.subTitle)
                    etNoteDesc.setText(notes.noteText)


                    selectedColor = notes.color!!

                    if (notes.imgPath != "") {
                        Log.d("CreateNoteFragment", "image identified")
                        selectedImagePath = notes.imgPath!!
                        imgNote.setImageBitmap(BitmapFactory.decodeFile(notes.imgPath))
                        layoutImage.visibility = View.VISIBLE
                        imgNote.visibility = View.VISIBLE
                        imgDelete.visibility = View.VISIBLE
                    } else {
                        layoutImage.visibility = View.GONE
                        imgNote.visibility = View.GONE
                        imgDelete.visibility = View.GONE
                    }

                    if (notes.webLink != "") {
                        webLink = notes.webLink!!
                        tvWebLink.text = notes.webLink
                        layoutWebUrl.visibility = View.VISIBLE
                        etWebLink.setText(notes.webLink)
                        imgUrlDelete.visibility = View.VISIBLE
                    } else {
                        imgUrlDelete.visibility = View.GONE
                        layoutWebUrl.visibility = View.GONE
                    }
                }
            }
        }


        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            BroadcastReceiver, IntentFilter("bottom_sheet_action")
        )

//        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val sdf = getDateTimeInstance()
        currentDate = sdf.format(Date())

        tvDateTime.text = currentDate

        imgDone.setOnClickListener {
            if (noteId != -1) {
                updateNote()
            } else {
                saveNote()
            }
        }

        imgBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        imgMore.setOnClickListener {
            val noteBottomSheetFragment = NotesBottomSheetFragment.newInstance(noteId)
            noteBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                "Note Bottom Sheet Fragment"
            )
        }

        imgDelete.setOnClickListener {
            selectedImagePath = ""
            layoutImage.visibility = View.GONE
        }

        imgUrlDelete.setOnClickListener {
            webLink = ""
            tvWebLink.visibility = View.GONE
            imgUrlDelete.visibility = View.GONE
            layoutWebUrl.visibility = View.GONE
        }

        btnOk.setOnClickListener {
            if (etWebLink.text.toString().trim().isNotEmpty()) {
                checkWebUrl()
            } else {
                Toast.makeText(requireContext(), "URL is required", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            if (noteId != -1) {
                tvWebLink.visibility = View.VISIBLE
                layoutWebUrl.visibility = View.GONE
            } else {
                layoutWebUrl.visibility = View.GONE
            }

        }

        tvWebLink.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(etWebLink.text.toString()))
            startActivity(intent)
        }


    }

    private fun updateNote() {

        etNoteTitle = requireActivity().findViewById(R.id.et_note_title) as EditText
        etNoteDesc = requireActivity().findViewById(R.id.et_note_desc) as EditText
        etSubTitle = requireActivity().findViewById(R.id.et_note_subtitle) as EditText

        launch {

            context?.let {
                val notes = NotesDatabase.getDatabase(it).notesDao().getSpecificNote(noteId)

                notes.title = etNoteTitle.text.toString()
                notes.subTitle = etSubTitle.text.toString()
                notes.noteText = etNoteDesc.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink

                NotesDatabase.getDatabase(it).notesDao().updateNote(notes)
                etNoteTitle.setText("")
                etSubTitle.setText("")
                etNoteDesc.setText("")
                layoutImage.visibility = View.GONE
                imgNote.visibility = View.GONE
                tvWebLink.visibility = View.GONE
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun saveNote() {

        etNoteTitle = requireActivity().findViewById(R.id.et_note_title) as EditText
        etNoteDesc = requireActivity().findViewById(R.id.et_note_desc) as EditText
        etSubTitle = requireActivity().findViewById(R.id.et_note_subtitle) as EditText

        if (etNoteTitle.text.isNullOrEmpty()) {
            Toast.makeText(context, "Note Title is Required", Toast.LENGTH_SHORT).show()
        } else if (etSubTitle.text.isNullOrEmpty()) {
            Toast.makeText(context, "Note Sub Title is Required", Toast.LENGTH_SHORT).show()
        } else if (etNoteDesc.text.isNullOrEmpty()) {
            Toast.makeText(context, "Note Description is Required", Toast.LENGTH_SHORT).show()
        } else {
            launch {
                val notes = Notes()
                notes.title = etNoteTitle.text.toString()
                notes.subTitle = etSubTitle.text.toString()
                notes.noteText = etNoteDesc.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink
                context?.let {
                    NotesDatabase.getDatabase(it).notesDao().insertNotes(notes)
                    etNoteDesc.setText("")
                    etNoteTitle.setText("")
                    etSubTitle.setText("")
                    layoutImage.visibility = View.GONE
                    imgNote.visibility = View.GONE
                    tvWebLink.visibility = View.GONE
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }

    }

    private fun deleteNote() {
        launch {
            context?.let {
                NotesDatabase.getDatabase(it).notesDao().deleteSpecificNote(noteId)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, istransition: Boolean) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if (istransition) {
            fragmentTransition.setCustomAnimations(
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
        }
        fragmentTransition.add(R.id.frame_layout, fragment)
            .addToBackStack(fragment.javaClass.simpleName)
    }

    private val BroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val actionColor = intent!!.getStringExtra("action")
            colorView = requireActivity().findViewById(R.id.colorView) as View
            layoutWebUrl = requireActivity().findViewById(R.id.layoutWebUrl) as LinearLayout
            layoutImage = requireActivity().findViewById(R.id.layoutImage) as RelativeLayout
            layoutNote = requireActivity().findViewById(R.id.cl_create_note) as ConstraintLayout
            imgNote = requireActivity().findViewById(R.id.imgCreateNote) as ImageView


            when (actionColor) {
                "Blue" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
//                    layoutNote.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Brown" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }


                "Green" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }


                "Orange" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }


                "Pink" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }

                "Grey" -> {
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))

                }

                "Image" -> {
                    readStorageTask()
                    layoutWebUrl.visibility = View.GONE
                }

                "WebUrl" -> {
                    layoutWebUrl.visibility = View.VISIBLE
                }

                "DeleteNote" -> {
                    deleteNote()
                }


                else -> {
                    layoutImage.visibility = View.GONE
                    imgNote.visibility = View.GONE
                    layoutWebUrl.visibility = View.GONE
                    selectedColor = intent.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                }
            }

        }
    }

    override fun onDestroyView() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(
            BroadcastReceiver
        )
        super.onDestroyView()
    }

    private fun checkWebUrl() {
        etWebLink = requireActivity().findViewById(R.id.etWebLink) as EditText
        btnOk = requireActivity().findViewById(R.id.btnOk) as Button
        btnCancel = requireActivity().findViewById(R.id.btnCancel) as Button
        tvWebLink = requireActivity().findViewById(R.id.tvWebLink) as TextView

        if (Patterns.WEB_URL.matcher(etWebLink.text.toString()).matches()) {
            layoutWebUrl.visibility = View.GONE
            etWebLink.isEnabled = false
            webLink = etWebLink.text.toString()
            tvWebLink.visibility = View.VISIBLE
            tvWebLink.text = etWebLink.text.toString()
        } else {
            Toast.makeText(requireContext(), "URL is not valid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasReadStoragePerm(): Boolean =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun readStorageTask() {
        if (hasReadStoragePerm()) {
            pickImageFromGallery()
        } else {
            mPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imgNote = requireActivity().findViewById(R.id.imgCreateNote) as ImageView
                layoutImage = requireActivity().findViewById(R.id.layoutImage) as RelativeLayout

                if (data != null) {
                    val selectedImageUrl = data.data
                    if (selectedImageUrl != null) {
                        try {
                            val inputStream =
                                requireActivity().contentResolver.openInputStream(selectedImageUrl)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            imgNote.setImageBitmap(bitmap)
                            imgNote.visibility = View.VISIBLE
                            layoutImage.visibility = View.VISIBLE

                            selectedImagePath = getPathFromUri(selectedImageUrl)!!
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {

            resultLauncher.launch(intent)
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String? = null
        val cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }


    private val mPermissionResult = registerForActivityResult(
        RequestPermission()
    ) { result ->
        when {
            result -> {
                Log.e(TAG, "onActivityResult: PERMISSION GRANTED")
                pickImageFromGallery()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Permissions Required")
                    .setMessage("This app may not work correctly without the requested permission.")
                    .setPositiveButton("Request again") { _, _ ->
                        readStorageTask()
                    }
                    .setNegativeButton("Dismiss", null)
                    .create()
                    .show()
            }
            else -> {
                Log.e(TAG, "onActivityResult: PERMISSION DENIED")
                AlertDialog.Builder(requireContext())
                    .setTitle("Permissions Required")
                    .setMessage("This app may not work correctly without the requested permission. Open the app settings screen to modify app permissions.")
                    .setPositiveButton("OK") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", requireContext().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show()
            }
        }
    }

}