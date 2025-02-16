package com.mystilt.shops.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mystilt.R
import com.mystilt.databinding.ShopPageFragmentBinding

class ShopPageFragment : BottomSheetDialogFragment() {


    companion object {
        const val TAG = "ShopPageFragment"
    }

    private lateinit var binding: ShopPageFragmentBinding
    private var userData: ShopPageModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ShopPageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userData?.let {
            // Bind the data to the views
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        val contentView = View.inflate(context, R.layout.shop_page_fragment, null)
        dialog.setContentView(contentView)

        dialog.setOnShowListener { it ->
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { layout ->
                val behaviour = BottomSheetBehavior.from(layout)
                behaviour.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
                layout.setBackgroundResource(R.drawable.rounded_top_corners)
            }
        }

        dialog.setCancelable(false)
        dialog.window?.attributes?.windowAnimations =
            com.google.android.material.R.style.Animation_Design_BottomSheetDialog
        return dialog
    }

    fun setData(data: ShopPageModel) {
        this.userData = data
    }

}