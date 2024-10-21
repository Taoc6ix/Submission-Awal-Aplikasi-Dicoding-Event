package com.taoc.submisionawal.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.taoc.submisionawal.data.response.Event
import com.taoc.submisionawal.databinding.FragmentEventDetailBinding

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getInt("eventId")
        if (eventId != null) {
            Log.d("EventDetailFragment", "Event ID: $eventId")

            viewModel.loadDetailEvent(eventId)
            observeViewModel()
        } else {
            showToast("Event ID not available")
        }
    }

    private fun observeViewModel() {
        viewModel.eventData.observe(viewLifecycleOwner) { event ->
            event?.let { updateUI(it) }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            showToast(errorMessage)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(event: Event) {
        binding.apply {
            eventName.text = event.name
            category.text = event.category
            ownerName.text = "Penyelenggara: ${event.ownerName}"
            beginTime.text = "Waktu Mulai: ${event.beginTime}"
            endTime.text = "Waktu Selesai: ${event.endTime}"
            val remainingQuota = event.quota?.minus(event.registrants!!)
            quota.text = "Sisa Kuota: $remainingQuota"
            eventDetails.text = event.summary
            description.text =
                HtmlCompat.fromHtml(event.description!!, HtmlCompat.FROM_HTML_MODE_LEGACY)

            Glide.with(this@EventDetailFragment)
                .load(event.mediaCover)
                .into(eventImage)

            openLinkButton.setOnClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(event.link)).also { intent ->
                    startActivity(intent)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}