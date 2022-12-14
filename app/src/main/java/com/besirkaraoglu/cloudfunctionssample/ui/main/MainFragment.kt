package com.besirkaraoglu.cloudfunctionssample.ui.main

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
import com.besirkaraoglu.cloudfunctionssample.databinding.FragmentMainBinding
import com.besirkaraoglu.cloudfunctionssample.model.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    val TAG = "MainFragment"

    private val binding by viewBinding(FragmentMainBinding::bind)
    private val viewModel by activityViewModels<MainViewModel>()

    val usersAdapter = MainRVAdapter { user -> adapterOnClick(user) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!viewModel.isUserRegistered()) {
            findNavController().navigate(R.id.action_mainFragment_to_registerFragment)
            return
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter = usersAdapter
        getUsers()
    }

    private fun adapterOnClick(user: User) {
        viewModel.setCurrentUserId(user.uid!!)
        findNavController().navigate(R.id.action_mainFragment_to_chatFragment)
    }

    private fun getUsers() {
        viewModel.getUsers()
        viewModel.users.observe(viewLifecycleOwner){ result ->
            when(result){
                is Resource.Empty -> {}
                is Resource.Error -> {
                    Log.e(TAG, "getUsers: ${result.message}")
                }
                is Resource.Loading -> {
                    Log.d(TAG, "getUsers: Users loading.")
                }
                is Resource.Success -> {
                    usersAdapter.submitList(result.data)
                }
            }
        }
    }


}