package com.player.musicoo.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ktx.immersionBar
import com.player.musicoo.App
import com.player.musicoo.R
import com.player.musicoo.adapter.ParentsVoiceAdapter
import com.player.musicoo.bean.Audio
import com.player.musicoo.databinding.FragmentImport2Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImportFragment : Fragment() {
    private lateinit var binding: FragmentImport2Binding
    private var parentsVoiceAdapter: ParentsVoiceAdapter? = null
    private var importAdapterList: MutableList<Audio> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImport2Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
//        binding.settingBtn.setOnClickListener {
//            startActivity(Intent(requireActivity(), SettingsActivity::class.java))
//        }
        binding.addBtn.setOnClickListener {
            binding.addBtn.visibility = View.GONE
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                openAudioPicker()
            }
        }
        importAdapterList.clear()
        importAdapterList.addAll(App.importList)
        parentsVoiceAdapter = ParentsVoiceAdapter(requireActivity(), importAdapterList)
        binding.importRv.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.importRv.adapter = parentsVoiceAdapter
    }

    private var requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openAudioPicker()
            } else {
                showExplanationDialog()
            }
        }

    private fun openAudioPicker() {
        binding.loadingLayout.visibility = View.VISIBLE
        getMusicFiles(requireActivity())
    }

    private fun showExplanationDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.permission_request))
            .setMessage(getString(R.string.permission_request_desc))
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        initImmersionBar()

        if (importAdapterList.isNotEmpty()) {
            binding.noContentLayout.visibility = View.GONE
        } else {
            binding.noContentLayout.visibility = View.VISIBLE
        }

        if (App.currentPlayingAudio != null) {
            if (App.importList.isNotEmpty()) {
                importAdapterList.clear()
                importAdapterList.addAll(App.importList)
                for ((index, audio) in importAdapterList.withIndex()) {
                    if (audio.file == App.currentPlayingAudio?.file) {
                        parentsVoiceAdapter?.notifyDataSetChanged()
                        break
                    }
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            initImmersionBar()
        }
    }

    private fun initImmersionBar() {
        immersionBar {
            statusBarDarkFont(true)
//            statusBarView(binding.view)
        }
    }

    private fun getAudioDuration(uri: Uri): Long {
        var duration = 0L
        try {
            val mediaPlayer = MediaPlayer.create(requireContext(), uri)
            duration = mediaPlayer.duration.toLong()
            mediaPlayer.release()
            return duration
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return duration
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getMusicFiles(context: Context): List<String> {
        val musicFiles = mutableListOf<String>()
        val contentResolver: ContentResolver = context.contentResolver

        // 定义查询参数
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        // 查询音频文件
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        cursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val data = cursor.getString(dataColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                val duration2 = getAudioDuration(contentUri)
                val audio = Audio(name, contentUri.toString(), "", duration2, false)

                musicFiles.add(contentUri.toString())
                // 如果你想要获取文件的具体路径，可以使用 data 变量
                // musicFiles.add(data)
                Log.d(
                    "ocean",
                    "name->${name} " +
                            "uri.toString()->${contentUri.toString()} " +
                            "duration2->$duration2 "
                            + "data->$data"
                )

                CoroutineScope(Dispatchers.IO).launch {
                    if (audio.duration > 0) {
                        App.databaseManager.insertAudioFile(audio)
                    }
                    withContext(Dispatchers.Main) {
                        App.initImportAudio {
                            importAdapterList.clear()
                            importAdapterList.addAll(App.importList)
                            parentsVoiceAdapter?.notifyDataSetChanged()
                            if (importAdapterList.isNotEmpty()) {
                                binding.noContentLayout.visibility = View.GONE
                            } else {
                                binding.noContentLayout.visibility = View.VISIBLE
                            }
                            binding.loadingLayout.visibility = View.GONE

                            binding.addBtn.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        return musicFiles
    }

}