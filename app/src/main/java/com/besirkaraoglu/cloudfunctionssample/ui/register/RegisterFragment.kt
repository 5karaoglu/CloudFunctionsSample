package com.besirkaraoglu.cloudfunctionssample.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.besirkaraoglu.cloudfunctionssample.MainViewModel
import com.besirkaraoglu.cloudfunctionssample.R
import com.besirkaraoglu.cloudfunctionssample.core.util.viewBinding
import com.besirkaraoglu.cloudfunctionssample.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val binding by viewBinding(FragmentRegisterBinding::bind)
    private val viewModel by activityViewModels<MainViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.buttonSave.setOnClickListener {
            viewModel.addUser(binding.etName.text.toString(),"")
            viewModel.addUserResult.observe(viewLifecycleOwner){ isSuccessful ->
                if (isSuccessful){
                    findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
                }else{
                    Toast.makeText(
                        requireContext(),
                        "Prcoess is not successful!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}