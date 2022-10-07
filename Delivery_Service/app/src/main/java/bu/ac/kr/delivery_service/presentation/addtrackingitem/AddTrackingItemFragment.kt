package bu.ac.kr.delivery_service.presentation.addtrackingitem

import android.app.Activity
import bu.ac.kr.delivery_service.databinding.FragmentAddTrackingItemBinding
import bu.ac.kr.delivery_service.entity.ShippingCompany
import bu.ac.kr.delivery_service.extension.toGone
import bu.ac.kr.delivery_service.extension.toVisible
import org.koin.android.scope.ScopeFragment
import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip

class AddTrackingItemFragment : ScopeFragment(), AddTrackingItemsContract.View {

    override val presenter: AddTrackingItemsContract.Presenter by inject()

    private var binding: FragmentAddTrackingItemBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAddTrackingItemBinding.inflate(inflater)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
        presenter.onViewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        presenter.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
    override fun showShippingCompaniesLoadingIndicator() {
        binding?.shippingCompanyProgressBar?.toVisible()
    }

    override fun hideShippingCompaniesLoadingIndicator() {
        binding?.shippingCompanyProgressBar?.toGone()
    }

    override fun showSaveTrackingItemIndicator() {
        binding?.saveButton?.apply{
            text = null
            isEnabled = false
        }
        binding?.saveProgressBar?.toVisible()
    }

    override fun hideSaveTrackingItemIndicator() {
        binding?.saveButton?.apply{
            text = "저장하기"
            isEnabled = true
        }
        binding?.saveProgressBar?.toGone()
    }

    override fun showCompanies(companies: List<ShippingCompany>) {
        companies.forEach { company ->
            binding?.chipGroup?.addView(
                Chip(context).apply {
                    text = company.name
                }
            )
        }
    }

    override fun enableSaveButton() {
        binding?.saveButton?.isEnabled = true
    }

    override fun disableSaveButton() {
        binding?.saveButton?.isEnabled = false
    }

    override fun showErrorToast(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

    override fun finish() {
        findNavController().popBackStack()
    }
    private fun bindView() {
        binding?.chipGroup?.setOnCheckedChangeListener { group, checkedId ->
            presenter.changeSelectedShippingCompany(group.findViewById<Chip>(checkedId).text.toString())
        }
        binding?.invoiceEditText?.addTextChangedListener { editable ->
            presenter.changeShippingInvoice(editable.toString())
        }
        binding?.saveButton?.setOnClickListener { _ ->
            presenter.saveTrackingItem()
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }

    private fun ClipboardManager.plainTextClip(): String? =
        if (hasPrimaryClip() && (primaryClipDescription?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true)) {
            primaryClip?.getItemAt(0)?.text?.toString()
        } else {
            null
        }
}
