package com.vijay.filepicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.vijay.filepicker.utils.openFile
import java.io.File

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
                handleMimeTypeVisibility()
            }

        }

        // add item selection listener so assign the selected position to 'mSelectedMimeTypePos' field variable
        mSpinnerMimeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    /**
     * hide show mime type drop down based on
     * file type selected
     */
    private fun handleMimeTypeVisibility() {
        when (mSelectedFileTypePos) {
            2, 3 -> {
                mSpinnerMimeType.visibility = View.GONE
            }
            else -> {
                mSpinnerMimeType.visibility = View.VISIBLE
            }
        }
    }


    /**
     * handle file picker as per selected option
     */
    private fun performFilePick() {
        when (mSelectedFileTypePos) {

            0 -> pickFile()

            1 -> pickMultipleFile()

            2 -> captureImage()

            3 -> captureVideo()

        }
    }

    /**
     * Open Camera and initiate capturing video and handle their states
     * such as onLoading where you show 'waiting UI' ,
     * such as onError where you show 'error UI'
     * such as onSuccess where you get the file
     */
    private fun captureVideo() {
        captureVideo {
            onSuccess { file ->
                setFileViewUI(arrayListOf(file))
                hideLoadingView()
            }
            onError {
                hideLoadingView()
            }

            onLoading {
                showLoadingView()
            }
        }
    }

    /**
     * Open Camera and initiate capturing image and handle their states
     * such as onLoading where you show 'waiting UI' ,
     * such as onError where you show 'error UI'
     * such as onSuccess where you get the file
     */
    private fun captureImage() {
        captureImage {
            onSuccess { file ->
                setFileViewUI(arrayListOf(file))
                hideLoadingView()
            }
            onError {
                hideLoadingView()
            }

            onLoading {
                showLoadingView()
            }
        }
    }

    /**
     * Open fileManager for where you pick file and handle their states
     * you can add 'mimeType' for restrict fileType, by default it will open all kind of file
     * such as onLoading where you show 'waiting UI' ,
     * such as onError where you show 'error UI'
     * such as onSuccess where you get the file
     */
    private fun pickFile() {
        pickFile {
            mimeType = mSpinnerMimeType.selectedItem.toString()
            onSuccess { file ->
                setFileViewUI(arrayListOf(file))
                hideLoadingView()
            }
            onError {
                hideLoadingView()
            }

            onLoading {
                showLoadingView()
            }
        }
    }

    /**
     * Open fileManager for where you pick multiple file and handle their states
     * you can add 'mimeType' for restrict fileType, by default it will open all kind of file
     * such as onLoading where you show 'waiting UI' ,
     * such as onError where you show 'error UI'
     * such as onSuccess where you get the file
     */
    private fun pickMultipleFile() {
        pickMultipleFile {
            mimeType = mSpinnerMimeType.selectedItem.toString()
            onSuccess { fileList ->
                setFileViewUI(fileList)
                hideLoadingView()
            }
            onError {
                hideLoadingView()
            }

            onLoading {
                showLoadingView()
            }
        }
    }

    private fun showLoadingView() {
        mButtonPickFile.isEnabled = false
        mButtonPickFile.text = getString(R.string.message_loading)
    }

    private fun hideLoadingView() {
        mButtonPickFile.isEnabled = true
        mButtonPickFile.text = getString(R.string.pick)
    }

    /**
     * set filepath and view UI
     */
    private fun setFileViewUI(files: List<File>) {
        mFileViewContainer.removeAllViews()
        files.forEach { file ->
            val view = LayoutInflater.from(this)
                .inflate(R.layout.item_file_view, mFileViewContainer, false)
            view.findViewById<TextView>(R.id.textFilePath).text = file.absolutePath
            view.findViewById<TextView>(R.id.textViewFile).setOnClickListener {
                openFile(file)
            }
            mFileViewContainer.addView(view)
        }
    }
}