package com.test.employee

import DialogDatePicker
import EmployeeItemAdapter
import EmployeeItemAdapterListener
import EmployeeModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.karyawan.Form2Act
import com.android.karyawan.Form2Act.REQ_CODE_REFRESH
import com.google.gson.Gson
import com.test.employee.databinding.EmployeeDataActivityBinding
import java.util.Calendar
import java.util.Date
import kotlin.system.exitProcess

class Form1Activity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName

    private lateinit var _binding: EmployeeDataActivityBinding

    private lateinit var dbHandler: DBManager

    private var mEmployeeItemsAdapter: EmployeeItemAdapter? = null
    private var mEmployeeModel: EmployeeModel? = null
    private var mJoinDateSelected: Date? = null
    private var mLastJoinDateSelected: Date? = null
    private var mCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = EmployeeDataActivityBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setSupportActionBar(_binding.toolbar)
        _binding.toolbar.title = "Form 1"
        _binding.toolbar.subtitle = "Filter Data"

        _binding.editBtn.isEnabled = false
        _binding.deleteBtn.isEnabled = false

        dbHandler = DBManager(this)
        initRecyclerView(this)
        setOnClickListener(this)

        mCalendar.time = Date()

        getEmployeeItems()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        getEmployeeItems()
        super.onResume()
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart")
        super.onRestart()
    }

    private fun initRecyclerView(context: Context){
        mEmployeeItemsAdapter = EmployeeItemAdapter(context)
            .setListener(object : EmployeeItemAdapterListener {
                override fun onClickItem(karyawan: EmployeeModel) {
                    mEmployeeModel = karyawan
                    setKaryawanToView(mEmployeeModel)
                }

                override fun itemsHasRefreshed() {
                    mEmployeeModel = null
                    _binding.editBtn.isEnabled = false
                    _binding.deleteBtn.isEnabled = false
                }
            })
        _binding.recyclerview.layoutManager = LinearLayoutManager(context)
        _binding.recyclerview.adapter = mEmployeeItemsAdapter
    }

    private fun setKaryawanToView(karyawan: EmployeeModel?){
        _binding.editBtn.isEnabled = false
        _binding.deleteBtn.isEnabled = false
        karyawan?.let {
            // _binding.firstAgeEdt.setText(it.nama)
            // _binding.firstAgeEdt.setText(it.usia.toString())
            // mJoinDateSelected = null
            // _binding.firstJoinDateEdt.text = it.tglMasukKerja
            mEmployeeModel = it
            _binding.editBtn.isEnabled = true
            _binding.deleteBtn.isEnabled = true
        }
    }

    private fun selectFirstJoinDate() {
        DialogDatePicker(this, mCalendar) { view, year, month, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, month)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            mJoinDateSelected = mCalendar.time
            _binding.firstJoinDateEdt.text = DateUtils.getDDMMMYYYYFrom(mJoinDateSelected)
        }.show()
    }

    private fun selectLastJoinDate() {
        DialogDatePicker(this, mCalendar) { view, year, month, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, month)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            mLastJoinDateSelected = mCalendar.time
            _binding.lastJoinDateEdt.text = DateUtils.getDDMMMYYYYFrom(mLastJoinDateSelected)
        }.show()
    }

    private fun setOnClickListener(context: Context) {
        _binding.searchBtn.setOnClickListener {
            val firstName = _binding.firstnameEdt.text.toString()
            val lastName = _binding.lastnameEdt.text.toString()

            var firstUsia = 0
            if(_binding.firstAgeEdt.text.toString().isNotEmpty()){
                firstUsia = Integer.parseInt(_binding.firstAgeEdt.text.toString())
            }
            var lastUsia = 0
            if(_binding.lastAgeEdt.text.toString().isNotEmpty()){
                lastUsia = Integer.parseInt(_binding.lastAgeEdt.text.toString())
            }
            getEmployeeItems(
                context = context,
                firstName = firstName,
                lastName = lastName,
                startDate = mJoinDateSelected,
                endDate = mLastJoinDateSelected,
                firstUsia = firstUsia,
                lastUsia = lastUsia,
            )
        }

        _binding.newBtn.setOnClickListener {
            startActivityForResult(Form2Act.getIntent(context, null), REQ_CODE_REFRESH)
        }

        _binding.editBtn.setOnClickListener {
            startActivity(Form2Act.getIntent(context, mEmployeeModel))
        }

        _binding.deleteBtn.setOnClickListener {
            mEmployeeModel?.let {
                deleteKaryawan(this, it.idKaryawan ?: -1)
            }
        }

        _binding.closeBtn.setOnClickListener {
            exitProcess(0)
        }

        _binding.firstJoinDateEdt.setOnClickListener {
            selectFirstJoinDate()
        }
        _binding.lastJoinDateEdt.setOnClickListener {
            selectLastJoinDate()
        }
    }

    private fun getEmployeeItems(){
        dbHandler.getItems(
            object: DBManagerListener.GetItems {
                override fun onSuccess(items: List<EmployeeModel>) {
                    Log.d(TAG, "items: ${Gson().toJson(items)}")
                    mEmployeeItemsAdapter?.setItems(items)
                }
            }
        )
    }

    private fun getEmployeeItems(context: Context,
                                 firstName: String,
                                 lastName: String,
                                 startDate: Date?,
                                 endDate: Date?,
                                 firstUsia: Int,
                                 lastUsia: Int,
    ){
        dbHandler.getItems(
            firstName = firstName,
            lastName = lastName,
            startDate = startDate,
            endDate = endDate,
            firstUsia = firstUsia,
            lastUsia = lastUsia,
            object: DBManagerListener.GetItems {
                override fun onSuccess(items: List<EmployeeModel>) {
                    Log.d(TAG, "items: ${Gson().toJson(items)}")
                    mEmployeeItemsAdapter?.setItems(items)
                }
            }
        )
    }

    private fun deleteKaryawan(context: Context, id: Int){
        dbHandler.deleteKaryawan(
            id = id,
            object: DBManagerListener.Delete {
                override fun onSuccess() {
                    Toast.makeText(context, "Data karyawan berhasil dihapus", Toast.LENGTH_SHORT)
                        .show()
                    getEmployeeItems()
                }
            }
        )
    }

    // override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    //   super.onActivityResult(requestCode, resultCode, data)
    //   if(resultCode == RESULT_OK){
    //     if(requestCode == REQ_CODE_REFRESH){
    //       val isRefresh = data?.extras?.getBoolean(ON_REFRESH, false) ?: false
    //       if(isRefresh){
    //         getEmployeeItems()
    //       }
    //     }
    //   }
    // }
}