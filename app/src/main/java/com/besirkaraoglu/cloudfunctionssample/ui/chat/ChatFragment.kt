package com.besirkaraoglu.cloudfunctionssample.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.besirkaraoglu.cloudfunctionssample.MainViewModel
import com.besirkaraoglu.cloudfunctionssample.R
import com.besirkaraoglu.cloudfunctionssample.core.util.viewBinding
import com.besirkaraoglu.cloudfunctionssample.databinding.FragmentChatBinding
import com.besirkaraoglu.cloudfunctionssample.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {

    private val binding by viewBinding(FragmentChatBinding::bind)
    private val viewModel by activityViewModels<MainViewModel>()

}