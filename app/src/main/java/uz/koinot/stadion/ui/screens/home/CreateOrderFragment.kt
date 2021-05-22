package uz.koinot.stadion.ui.screens.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.BaseFragment
import uz.koinot.stadion.MainActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.UserAdapter
import uz.koinot.stadion.data.model.CreateOrder
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentCreateOrderBinding
import uz.koinot.stadion.utils.*
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class CreateOrderFragment : BaseFragment(R.layout.fragment_create_order) {

    @Inject
    lateinit var pref:LocalStorage

    private val viewModel: CreateOrderViewModel by viewModels()
    private var _bn: FragmentCreateOrderBinding? = null
    private val bn get() = _bn!!
    private var stadiumId = 0
    private val adapter = UserAdapter()
    private var currentPhoneNumber = ""
    var job:Job? = null

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

            myLocation.setOnClickListener {
                currentPhoneNumber = pref.phoneNumber
                inputPhoneNumber.setText(pref.phoneNumber)
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
                    val date1 = "${hourOfDay.getString()}:${minute.getString()}"
                    inputStartDate.setText(date1)
                    val date2 = inputEndDate.text.toString()
                    if(date2.isNotEmpty()){
                        viewModel.getOrderPrice(stadiumId,date1,date2)
                    }
                },true).show(parentFragmentManager,"aaa")
            }
            inputEndDate.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    val date1 = "${hourOfDay.getString()}:${minute.getString()}"
                    inputEndDate.setText(date1)
                    val date2 = inputStartDate.text.toString()
                    if(date2.isNotEmpty()){
                        viewModel.getOrderPrice(stadiumId,date2,date1)
                    }
                },true).show(parentFragmentManager,"ttt")
            }
            inputPhoneNumber.addTextChangedListener { text ->
                if(text.toString().length > 4 && text.toString() != currentPhoneNumber){
                    searchJob(text.toString())
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

    private fun searchJob(toString: String) {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            delay(1000)
            viewModel.searchUser(toString)
        }
    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.createOrderFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showProgressDialog(false)
                        clearData()
                        showMessage("Successfully created order")
                    }
                    is UiStateObject.ERROR ->{
                        showProgressDialog(false)
                        showMessage("Error Please Try again")
                    }
                    is UiStateObject.LOADING ->{
                        showProgressDialog(true)
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
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.orderPriceFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        bn.loadingPrice.isVisible = false
                        bn.textOrderPrice.isVisible = true
                        if(it.data.toString().contains("-")){
                            showMessage("Vaqt kiritishda xatolik")
                            bn.textOrderPrice.text = "0"
                            bn.inputEndDate.setText("")
                        }else
                             bn.textOrderPrice.text = it.data.toMoneyFormat()
                    }
                    is UiStateObject.ERROR ->{
                        bn.loadingPrice.isVisible = false
                        bn.textOrderPrice.isVisible = true
//                        showMessage("Error Please Try again")
                    }
                    is UiStateObject.LOADING ->{
                        bn.loadingPrice.isVisible = true
                        bn.textOrderPrice.isVisible = false
//                        showMessage("Loading")
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