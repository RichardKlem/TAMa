package com.example.tama.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tama.R
import com.example.tama.databinding.FragmentSettingsBinding
import com.example.tama.ui.events.EventsViewModel
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment() {
    private lateinit var settingsAdapter: SettingsAdapter
    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val header : MutableList<String> = ArrayList()
    private val body : MutableList<MutableList<String>> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notifications : MutableList<String> = ArrayList()
        notifications.add("Setting 1")
        notifications.add("Setting 2")

        header.add("Notifications")
        body.add(notifications)

        settingsAdapter = SettingsAdapter(context, notification_list_view, header, body)
        notification_list_view.setAdapter(settingsAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}