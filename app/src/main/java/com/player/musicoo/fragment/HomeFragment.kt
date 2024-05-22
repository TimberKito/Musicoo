package com.player.musicoo.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ktx.immersionBar
import com.player.musicoo.App
import com.player.musicoo.R
import com.player.musicoo.adapter.RealHumanVoiceAdapter
import com.player.musicoo.adapter.SoundsOfAppliancesAdapter
import com.player.musicoo.adapter.SoundsOfNatureAdapter
import com.player.musicoo.databinding.FragmentHome2Binding
import com.player.musicoo.databinding.FragmentHomeBinding
import com.player.musicoo.util.GridSpacingItemDecoration
import com.player.musicoo.util.HorizontalSpaceItemDecoration

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHome2Binding
    private var realHumanVoiceAdapter: RealHumanVoiceAdapter? = null
    private var soundsOfAppliancesAdapter: SoundsOfAppliancesAdapter? = null
    private var soundsOfNatureAdapter: SoundsOfNatureAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHome2Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initImmersionBar() {
        immersionBar {
            statusBarDarkFont(true)
//            statusBarView(binding.view)
        }
    }

    private fun initView() {
        if (App.resourcesList.categories.isNotEmpty()) {
            binding.soundsName.text = App.resourcesList.categories[1].name
            binding.natureName.text = App.resourcesList.categories[2].name
        }
        if (App.realHumanVoiceList.isNotEmpty()) {
            realHumanVoiceAdapter = RealHumanVoiceAdapter(requireActivity(), App.realHumanVoiceList)
            binding.realRv.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.realRv.addItemDecoration(HorizontalSpaceItemDecoration(requireActivity()))
            binding.realRv.adapter = realHumanVoiceAdapter
        }
        if (App.soundsOfAppliancesList.isNotEmpty()) {
            soundsOfAppliancesAdapter =
                SoundsOfAppliancesAdapter(requireActivity(), App.soundsOfAppliancesList)
            binding.soundsRv.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            binding.soundsRv.addItemDecoration(HorizontalSpaceItemDecoration(requireActivity()))
            binding.soundsRv.adapter = soundsOfAppliancesAdapter
        }
        if (App.soundsOfNatureList.isNotEmpty()) {
            soundsOfNatureAdapter =
                SoundsOfNatureAdapter(requireActivity(), App.soundsOfNatureList)
            binding.natureRv.layoutManager =
                GridLayoutManager(requireActivity(), 3, GridLayoutManager.HORIZONTAL, false)
            binding.natureRv.addItemDecoration(GridSpacingItemDecoration(requireActivity(), 10, 3))
            binding.natureRv.adapter = soundsOfNatureAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if (App.currentPlayingAudio != null) {
            if (App.realHumanVoiceList.isNotEmpty()) {
                for ((index, audio) in App.realHumanVoiceList.withIndex()) {
                    if (audio.file == App.currentPlayingAudio?.file) {
                        notifyDataSetChanged()
                        break
                    }
                }
            }
            if (App.soundsOfAppliancesList.isNotEmpty()) {
                for ((index, audio) in App.soundsOfAppliancesList.withIndex()) {
                    if (audio.file == App.currentPlayingAudio?.file) {
                        notifyDataSetChanged()
                        break
                    }
                }
            }
            if (App.soundsOfNatureList.isNotEmpty()) {
                for ((index, audio) in App.soundsOfNatureList.withIndex()) {
                    if (audio.file == App.currentPlayingAudio?.file) {
                        notifyDataSetChanged()
                        break
                    }
                }
            }
        }
        initImmersionBar()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            initImmersionBar()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyDataSetChanged(){
        soundsOfAppliancesAdapter?.notifyDataSetChanged()
        realHumanVoiceAdapter?.notifyDataSetChanged()
        soundsOfNatureAdapter?.notifyDataSetChanged()
    }

}