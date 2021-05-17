package com.oneibc.feature.jurisdiction

import android.widget.SearchView
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.oneibc.MainActivity
import com.oneibc.R
import com.oneibc.common.BaseFragment
import com.oneibc.databinding.FragmentSearchFilterBinding
import com.oneibc.domain.entities.Status
import com.oneibc.feature.jurisdiction.adapter.FaqsAdapter
import com.oneibc.feature.jurisdiction.adapter.JurisdictionAdapter
import com.oneibc.feature.jurisdiction.model_temp.FaqData
import com.oneibc.feature.services_detail.ServiceDetailViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class SearchFilterFragment :
    BaseFragment<ServiceDetailViewModel, FragmentSearchFilterBinding>(R.layout.fragment_search_filter) {

    override val viewModel: ServiceDetailViewModel by viewModel()
    override val binding: FragmentSearchFilterBinding by viewBinding()

    private val args: SearchFilterFragmentArgs by navArgs()

    private val jurisdictionAdapter = JurisdictionAdapter()
    private val faqsAdapter = FaqsAdapter()

    override fun initControl() {
        (activity as MainActivity).apply {
            initToolbar(backButton = true,
                backButtonTitle = when (args.typeSearch) {
                    SearchFilterType.JURISDICTION_1 -> "Notifications"
                    else -> "Unknown"
                },
                backButtonListener = {
                    backToPrevious()
                })
            enableBottomNavigation(false)
        }

        binding.searchView.queryHint=when(args.typeSearch){
            SearchFilterType.JURISDICTION_1->"Enter the Jurisdiction"
            SearchFilterType.FAQ->"Search faqs..."
            else->""
        }

        binding.titleSearch.text=when(args.typeSearch){
            SearchFilterType.JURISDICTION_1->"Jurisdictions"
            SearchFilterType.FAQ->"Company Formation Services - FAQs"
            else->""
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String?): Boolean {
                when(args.typeSearch){
                    SearchFilterType.JURISDICTION_1->jurisdictionAdapter.search(newText)
                    SearchFilterType.FAQ->faqsAdapter.search(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                when(args.typeSearch){
                    SearchFilterType.JURISDICTION_1->jurisdictionAdapter.search(query)
                    SearchFilterType.FAQ->faqsAdapter.search(query)
                }
                binding.searchView.clearFocus()
                return true
            }

        })
    }

    override fun initUI() {
        binding.rvSearchFilter.apply {
            adapter = when(args.typeSearch){
                SearchFilterType.JURISDICTION_1->jurisdictionAdapter
                SearchFilterType.FAQ->faqsAdapter
                else->faqsAdapter
            }
        }
    }

    override fun initEvent() {
        jurisdictionAdapter.callBackItemClick = {
            when (args.typeSearch) {
                SearchFilterType.JURISDICTION_1 -> {
                    navigate(
                        SearchFilterFragmentDirections.actionSearchFilterFragmentToCountryUpdatesFragment(
                            jurisdictionAdapter.resultList[it].name,
                            jurisdictionAdapter.resultList[it].code
                        )
                    )
                }
            }
        }

        viewModel.jurisdictionLiveData.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { data ->
                when (data.responseType) {
                    Status.ERROR -> {
                        hideLoadingDialog()
                    }

                    Status.LOADING -> {
                        showLoadingDialog()
                    }

                    Status.SUCCESSFUL -> {
                        hideLoadingDialog()
                        data.data?.let { jurisdictionResult ->
                            when(args.typeSearch){
                                SearchFilterType.JURISDICTION_1->jurisdictionAdapter.addAll(jurisdictionResult.jurisdictions)
                                SearchFilterType.FAQ->faqsAdapter.addAll(FaqData.create())
                            }
                        }
                    }
                }
            }
        })
    }

    override fun initConfig() {
        if (!firstCallApi) {
            firstCallApi = true
            viewModel.createRequestJurisdiction()
        }
    }
}