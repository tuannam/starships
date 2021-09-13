package au.com.starships.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import au.com.starships.R
import au.com.starships.databinding.ErrorDialogBinding
import org.parceler.Parcel
import org.parceler.Parcels

class ErrorDialog : DialogFragment() {
    @Parcel
    enum class DIALOG_TYPE {
        OK_ONLY, YES_OK
    }

    enum class BUNDLE_ARGS {
        MESSAGE, DIALOG_TYPE
    }

    companion object {
        val mutex = Object()
        var displayed = false

        fun createOK(message: String, okClicked: () -> Unit): ErrorDialog {
            val args = Bundle()
            args.putString(BUNDLE_ARGS.MESSAGE.name, message)
            args.putParcelable(BUNDLE_ARGS.DIALOG_TYPE.name, Parcels.wrap(DIALOG_TYPE.OK_ONLY))
            val fragment = ErrorDialog()
            fragment.yesClicked = okClicked
            fragment.setStyle(STYLE_NORMAL, R.style.Theme_ErrorDialog)
            fragment.arguments = args
            return fragment
        }

        fun createYesNo(
            message: String,
            yesClicked: () -> Unit,
            noClicked: () -> Unit
        ): ErrorDialog {
            val args = Bundle()
            args.putString(BUNDLE_ARGS.MESSAGE.name, message)
            args.putParcelable(BUNDLE_ARGS.DIALOG_TYPE.name, Parcels.wrap(DIALOG_TYPE.YES_OK))
            val fragment = ErrorDialog()
            fragment.yesClicked = yesClicked
            fragment.noClicked = noClicked
            fragment.setStyle(STYLE_NORMAL, R.style.Theme_ErrorDialog)
            fragment.arguments = args
            return fragment
        }
    }


    lateinit var binding: ErrorDialogBinding
    var yesClicked: (() -> Unit)? = null
    var noClicked: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ErrorDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message = arguments?.getString(BUNDLE_ARGS.MESSAGE.name)
        val type =
            Parcels.unwrap<DIALOG_TYPE>(arguments?.getParcelable(BUNDLE_ARGS.DIALOG_TYPE.name))
        binding.errorDialogText.text = message

        when (type) {
            DIALOG_TYPE.OK_ONLY -> {
                binding.errorDialogBorderBtn.visibility = View.GONE
                binding.errorDialogSolidBtn.text = "OK"
                binding.errorDialogSolidBtn.setOnClickListener {
                    dismiss()
                    yesClicked?.let { it() }
                }
            }
            DIALOG_TYPE.YES_OK -> {
                binding.errorDialogSolidBtn.text = "No"
                binding.errorDialogSolidBtn.setOnClickListener {
                    dismiss()
                    noClicked?.let { it() }
                }
                binding.errorDialogBorderBtn.text = "Yes"
                binding.errorDialogBorderBtn.setOnClickListener {
                    dismiss()
                    yesClicked?.let { it() }
                }

            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        synchronized(mutex) {
            if (!displayed) {
                displayed = true
                try {
                    super.show(manager, tag)
                } catch (e: IllegalStateException) {
                }
            }
        }
    }

    override fun dismiss() {
        synchronized(mutex) {
            try {
                super.dismiss()
            } catch (e: IllegalStateException) {
            }
            displayed = false
        }
    }

    fun display(activity: FragmentActivity) {
        show(activity.supportFragmentManager, ErrorDialog::class.java.simpleName)
    }
}