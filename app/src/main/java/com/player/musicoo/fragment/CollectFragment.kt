package com.player.musicoo.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.player.musicoo.adapter.CollectAdapter
import com.player.musicoo.bean.Audio
import com.player.musicoo.database.CollectViewModel
import com.player.musicoo.databinding.FragmentImport2Binding

class CollectFragment : Fragment() {

    private lateinit var binding: FragmentImport2Binding
    private lateinit var viewModel: CollectViewModel
    private lateinit var collectAdapter: CollectAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentImport2Binding.inflate(layoutInflater)

        val filter = IntentFilter("ACTION_DATABASE_UPDATED")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mDatabaseUpdatedReceiver, filter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        collectAdapter = CollectAdapter(requireContext())
        binding.recyclerLike.run {
            adapter = collectAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.update()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this)[CollectViewModel::class.java]

        viewModel.getList().observe(viewLifecycleOwner, Observer {
            binding.collectCard.isVisible = it.isEmpty()
            collectAdapter.updateData(it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mDatabaseUpdatedReceiver)
    }

    private val mDatabaseUpdatedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                if (intent.action.equals("ACTION_DATABASE_UPDATED")) {
                    viewModel.update()
                }
            }
        }
    }

}