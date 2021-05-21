package uz.koinot.stadion.ui.screens.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.MainActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.UserAdapter
import uz.koinot.stadion.data.model.CreateOrder
import uz.koinot.stadion.databinding.FragmentCreateOrderBinding
import uz.koinot.stadion.databinding.FragmentOderBinding
import uz.koinot.stadion.utils.*
import java.util.*


@AndroidEntryPoint
class CreateOrderFragment : Fragment(R.layout.fragment_create_order) {

    private val viewModel: CreateOrderViewModel by viewModels()
    private var _bn: FragmentCreateOrderBinding? = null
    private val bn get() = _bn!!
    private var stadiumId = 0
    private val adapter = UserAdapter()
    private var currentPhoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments != null){
            stadiumId = arguments?.getInt(CONSTANTS.STADION_ID)!!
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentCreateOrderBinding.bind(view)

        bn.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            rvUsers.adapter = adapter

            adapter.setOnClickListener {
                adapter.clear()
                currentPhoneNumber = it.phoneNumber
                inputPhoneNumber.setText(it.phoneNumber)
                inputFirstName.setText(it.firstName)
                inputLastName.setText(it.lastName)
            }
            inputDay.setOnClickListener {
                val dialog = DatePickerDialog.newInstance{ _, year, monthOfYear, dayOfMonth ->
                    inputDay.setText("${year.getString()}-${monthOfYear.getString()}-${dayOfMonth.getString()}")
                }
                dialog.minDate = Calendar.getInstance()
                    dialog.show(parentFragmentManager,"BBB")

            }
            inputStartDate.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    inputStartDate.setText("${hourOfDay.getString()}:${minute.getString()}")
                },true).show(parentFragmentManager,"aaa")
            }
            inputEndDate.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    inputEndDate.setText("${hourOfDay.getString()}:${minute.getString()}")
                },true).show(parentFragmentManager,"ttt")
            }
            inputPhoneNumber.addTextChangedListener { text ->
                if(text.toString().length > 4 && text.toString() != currentPhoneNumber){
                    viewModel.searchUser(text.toString())
                    currentPhoneNumber = text.toString()
                }
            }
            btnCreateOrder.setOnClickListener {
                val number = inputPhoneNumber.text.toString().trim()
                val day = inputDay.text.toString().trim()
                val startTime = inputStartDate.text.toString().trim()
                val endTime = inputEndDate.text.toString().trim()
                val firstName = inputFirstName.text.toString().trim()
                val lastName = inputLastName.text.toString().trim()
                if(number.isNotEmpty() && day.isNotEmpty() && startTime.isNotEmpty()
                    && endTime.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()){
                    viewModel.createOrder(
                        CreateOrder(null,stadiumId,day+"T$startTime:00.000000",day+"T$endTime:00.000000",day+"T$startTime:00.000000",firstName,lastName,number))
                }else{
                    showMessage("Iltimos qatorlarni to'ldiring")
                }
            }
        }
        collects()
    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.createOrderFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        clearData()
                        showMessage("Successfully created order")
                    }
                    is UiStateObject.ERROR ->{
                        showMessage("Error Please Try again")
                    }
                    is UiStateObject.LOADING ->{
                        showMessage("Loading")
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.searchUserFlow.collect {
                when(it){
                    is UiStateList.SUCCESS ->{
                        if (it.data != null)
                        adapter.submitList(it.data)
                    }
                    is UiStateList.ERROR ->{
                        showMessage("Error Please Try again")
                    }
                    is UiStateList.LOADING ->{
                        showMessage("Loading")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun clearData() {
        bn.apply {
            inputPhoneNumber.text?.clear()
            inputDay.text?.clear()
            inputStartDate.text?.clear()
            inputEndDate.text?.clear()
            inputFirstName.text?.clear()
            inputLastName.text?.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}