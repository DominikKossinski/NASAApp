package com.example.nasa_app.architecture

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.nasa_app.R
import com.example.nasa_app.api.models.ApiError
import kotlinx.coroutines.flow.collect
import pl.kossa.myflights.architecture.BaseViewModel
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment() {

    protected abstract val viewModel: VM

    protected lateinit var binding: VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vbType = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]
        val vbClass = vbType as Class<VB>
        val method = vbClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        binding = method.invoke(null, inflater, container, false) as VB
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickListeners()
        collectFlow()
    }

    protected open fun setupOnClickListeners() {}

    protected open fun collectFlow() {
//        TODO
//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            viewModel.signOutFlow.collect {
//                when (findNavController().graph.id) {
//                    R.id.main_nav_graph -> {
//                        Navigation.findNavController(requireActivity(), R.id.mainNavHostFragment)
//                            .navigate(MainNavGraphDirections.goToLoginActivity())
//                    }
//                    R.id.lists_nav_graph -> {
//                        Navigation.findNavController(requireActivity(), R.id.mainNavHostFragment)
//                            .navigate(MainNavGraphDirections.goToLoginActivity())
//                    }
//                }
//                (activity as? MainActivity)?.finish()
//            }
//        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.backFlow.collect {
                Log.d("MyLog", "Collecting back navigation")
                when (findNavController().graph.id) {
                    R.id.main_nav_graph -> {
                        Navigation.findNavController(
                            requireActivity(),
                            R.id.mainNavHostFragment
                        )
                            .popBackStack()
                    }
//                    TODO
//                    R.id.lists_nav_graph -> {
//                        Navigation.findNavController(
//                            requireActivity(),
//                            R.id.listsNavHostFragment
//                        )
//                            .popBackStack()
//                    }
//                    R.id.login_nav_graph -> {
//                        Navigation.findNavController(
//                            requireActivity(),
//                            R.id.login_nav_host_fragment
//                        ).popBackStack()
//                    }
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getNavDirectionsFlow().collect {
                Log.d("MyLog", "Collecting navigation")
                Log.d("MyLog", "${findNavController().currentDestination?.label}")
                when (findNavController().graph.id) {
                    R.id.main_nav_graph -> {
                        Log.d("MyLog", "MainGraph $it")
                        Navigation.findNavController(
                            requireActivity(),
                            R.id.mainNavHostFragment
                        )
                            .navigate(it)
                    }
//                    TODO
//                    R.id.lists_nav_graph -> {
//                        Log.d("MyLog", "ListGraph $it")
//                        Navigation.findNavController(
//                            requireActivity(),
//                            R.id.mainNavHostFragment
//                        )
//                            .navigate(it)
//                    }
//                    R.id.login_nav_graph -> {
//                        findNavController().navigate(it)
//                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.toastMessage.collect {
                Log.d("MyLog", "Collecting error: $it")
                it?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            }

        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.apiErrorFlow.collect {
                it?.let { handleApiError(it) }
            }
        }
    }

    protected abstract fun handleApiError(apiError: ApiError)

}