package com.taoc.submisionawal.ui.event_available

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.taoc.submisionawal.R
import com.taoc.submisionawal.data.response.ListEventsItem
import com.taoc.submisionawal.databinding.FragmentEventAvailableBinding

class EventAvailableFragment : Fragment() {

    private var _binding: FragmentEventAvailableBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAvailableAdapter
    private lateinit var viewModel: EventAvailableViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventAvailableBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAvailableAdapter { eventId ->
            val action = EventAvailableFragmentDirections.actionNavigationEventAvailableToEventDetail(eventId)
            view?.findNavController()?.navigate(action)
        }

        binding.rvEventAvailable.layoutManager = LinearLayoutManager(requireContext())
        val itemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        binding.rvEventAvailable.addItemDecoration(itemDecoration)
        binding.rvEventAvailable.adapter = eventAdapter
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(EventAvailableViewModel::class.java)

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.eventResponse.observe(viewLifecycleOwner) { eventResponse ->
            if (eventResponse != null) {
                setEventData(eventResponse.listEvents)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                showErrorMessage(errorMessage)
            }
        }

        viewModel.fetchActiveEvents()
    }

    private fun setEventData(events: List<ListEventsItem?>?) {
        eventAdapter.submitList(events)
        binding.tvErrorMessage.visibility = View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.tvErrorMessage.text = message
        binding.tvErrorMessage.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}