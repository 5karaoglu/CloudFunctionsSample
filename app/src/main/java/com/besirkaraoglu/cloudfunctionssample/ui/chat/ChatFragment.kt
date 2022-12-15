package com.besirkaraoglu.cloudfunctionssample.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.besirkaraoglu.cloudfunctionssample.MainViewModel
import com.besirkaraoglu.cloudfunctionssample.R
import com.besirkaraoglu.cloudfunctionssample.core.util.Resource
import com.besirkaraoglu.cloudfunctionssample.core.util.viewBinding
import com.besirkaraoglu.cloudfunctionssample.databinding.FragmentChatBinding
import com.besirkaraoglu.cloudfunctionssample.databinding.FragmentMainBinding
import com.besirkaraoglu.cloudfunctionssample.model.Message
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {
    val TAG = "ChatFragment"

    private val binding by viewBinding(FragmentChatBinding::bind)
    private val viewModel by activityViewModels<MainViewModel>()

    private lateinit var receiverUid: String
    private lateinit var adapter: ChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.userId.value == null){
            Log.e(TAG, "onViewCreated: No userId set!")
            findNavController().navigateUp()
            return
        }else{
            receiverUid = viewModel.userId.value ?: ""
            adapter = ChatAdapter(receiverUid){message -> adapterOnClick(message) }
        }
        binding.buttonSend.setOnClickListener {
            sendMessage()
        }
        initObservers()
        initRecyclerView()
        getMessages()
    }

    private fun initObservers() {
        viewModel.sendMessageResult.observe(viewLifecycleOwner){ result ->
            when(result){
                is Resource.Empty -> {}
                is Resource.Error -> {
                    Log.e(TAG, "initObservers: ${result.message ?: "Send message failed!"}")
                }
                is Resource.Loading -> {
                    Log.d(TAG, "initObservers: Send message started.")
                }
                is Resource.Success -> {
                    Log.d(TAG, "initObservers: Message sent.")
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun adapterOnClick(message: Message){

    }

    private fun sendMessage(){
        val text = binding.etText.text.toString()
        if (text.isNotEmpty()){
            viewModel.sendMessage(receiverUid,text)
        }
    }

    private fun getMessages() {
        viewModel.getMessages()
        viewModel.messages.observe(viewLifecycleOwner){ result ->
            when(result){
                is Resource.Empty -> {

                }
                is Resource.Error -> {
                    Log.e(TAG, "getMessages: ${result.message}")
                }
                is Resource.Loading -> {
                    Log.d(TAG, "getMessages: Loading.")
                }
                is Resource.Success -> {
                    Log.d(TAG, "getMessages: Success. ${result.data!!.size}")
                    adapter.submitList(result.data)
                }
            }
        }

    }

}