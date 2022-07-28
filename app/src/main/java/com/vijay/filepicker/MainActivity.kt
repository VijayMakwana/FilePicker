package com.vijay.filepicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {

    private lateinit var mButtonPickFile: Button
    private lateinit var mSpinnerFileType: Spinner
    private lateinit var mSpinnerMimeType: Spinner
    private lateinit var mFileViewContainer: LinearLayout

    private var mSelectedFileTypePos = 0
    private var mSelectedMimeTypePos = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }


    private fun initViews() {

        mButtonPickFile = findViewById(R.id.btnPickFile)
        mSpinnerFileType = findViewById(R.id.spinnerFileType)
        mSpinnerMimeType = findViewById(R.id.spinnerMimeType)
        mFileViewContainer = findViewById(R.id.fileViewContainer)

        // setup file picker options
        setupDropDownOptions()

        // add onClick for file pick
        mButtonPickFile.setOnClickListener {
            performFilePick()
        }
    }

    private fun performFilePick() {

    }

    private fun setupDropDownOptions() {
        ArrayAdapter.createFromResource(
            this,
            R.array.pickerOptions,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            mSpinnerFileType.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.mimeTypeOptions,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            mSpinnerMimeType.adapter = adapter
        }

        // add item selection listener so assign the selected position to 'mSelectedFileTypePos' field variable
        mSpinnerFileType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mSelectedFileTypePos = position
            }

        }

        // add item selection listener so assign the selected position to 'mSelectedMimeTypePos' field variable
        mSpinnerFileType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mSelectedMimeTypePos = position
            }

        }
    }
}