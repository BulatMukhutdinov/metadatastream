package tat.mukhutdinov.metadatastream.ui

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import tat.mukhutdinov.metadatastream.R
import tat.mukhutdinov.metadatastream.databinding.MetaStreamBinding
import tat.mukhutdinov.metadatastream.ui.boundary.MetaStreamViewModel
import tat.mukhutdinov.metadatastream.viewmodel.MetaStreamLifecycleAwareViewModel

class MetaStreamActivity : AppCompatActivity() {

    private val viewModel: MetaStreamViewModel by viewModel<MetaStreamLifecycleAwareViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<MetaStreamBinding>(this, R.layout.meta_stream)
        binding.viewModel = viewModel.binding
        binding.lifecycleOwner = this

        viewModel.toastMessage.observe(this, Observer {
            Toast.makeText(this, it, LENGTH_SHORT).show()
        })
    }
}