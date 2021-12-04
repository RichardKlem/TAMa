package com.example.tama.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tama.R
import com.example.tama.databinding.FragmentSettingsBinding
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var settingsAdapter: SettingsAdapter

    private val header : MutableList<String> = ArrayList()
    private val body : MutableList<MutableList<String>> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notifikace : MutableList<String> = ArrayList()
        notifikace.add("Setting 1")
        notifikace.add("Setting 2")

        header.add("Notifikace")
        body.add(notifikace)

        settingsAdapter = SettingsAdapter(context, notification_list_view, header, body)
        notification_list_view.setAdapter(settingsAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}