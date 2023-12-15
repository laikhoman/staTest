package com.test.employee.form2

import DialogDatePicker
import EmployeeModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.test.employee.DBManager
import com.test.employee.DBManagerListener
import com.test.employee.DateUtils
import com.test.employee.form2.Form2Act.KARYAWAN
import com.test.employee.form2.Form2Act.ON_REFRESH
import com.test.employee.databinding.ActivityForm2Binding
import java.util.*

object Form2Act {
    @JvmStatic val REQ_CODE_REFRESH = 3762
    @JvmStatic val ON_REFRESH = "onRefresh"
    @JvmStatic val KARYAWAN = "karyawan"
    @JvmStatic fun getIntent(context: Context, karyawan: EmployeeModel?): Intent {
        return Intent(
            context, Form2Activity::class.java
        )
            .putExtra(KARYAWAN, karyawan)
    }
}

class Form2Activity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName

    private lateinit var _binding: ActivityForm2Binding

    private lateinit var dbHandler: DBManager

    private var mEmployeeModel: EmployeeModel? = null
    private var mJoinDateSelected: Date? = null
    private var mCalendar = Calendar.getInstance()
    private var mDialogDatePicker: DialogDatePicker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityForm2Binding.inflate(layoutInflater)
        setContentView(_binding.root)
        setSupportActionBar(_binding.toolbar)

        _binding.contentForm2Icl.saveCreateBtn.visibility = View.GONE
        _binding.contentForm2Icl.saveUpdateBtn.visibility = View.GONE

        dbHandler = DBManager(this)
        initDatePicker()
        setOnClickListener(this)

        mEmployeeModel = intent?.extras?.getSerializable(KARYAWAN) as EmployeeModel?
        setKaryawanToView(mEmployeeModel)
    }

    private fun initDatePicker(){
        mJoinDateSelected = Date()
        mCalendar.time = mJoinDateSelected
        mDialogDatePicker = DialogDatePicker(this, mCalendar) { view, year, month, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, month)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            mJoinDateSelected = mCalendar.time
            _binding.contentForm2Icl.firstJoinDateEdt.text = DateUtils.getDDMMMYYYYFrom(mJoinDateSelected)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setKaryawanToView(karyawanModel: EmployeeModel?){
        _binding.contentForm2Icl.titleTv.text = "Add karyawan"
        _binding.contentForm2Icl.idEdt.isEnabled = true
        _binding.contentForm2Icl.saveCreateBtn.visibility = View.VISIBLE
        _binding.contentForm2Icl.saveUpdateBtn.visibility = View.GONE
        karyawanModel?.let { karyawan ->
            _binding.contentForm2Icl.titleTv.text = "Update karyawan"

            _binding.contentForm2Icl.idEdt.setText(karyawan.idKaryawan.toString())
            _binding.contentForm2Icl.idEdt.isEnabled = false
            _binding.contentForm2Icl.firstnameEdt.setText(karyawan.namaKaryawan)
            _binding.contentForm2Icl.firstAgeEdt.setText(karyawan.usia.toString())
            mJoinDateSelected = karyawan.tglMasukKerja
            _binding.contentForm2Icl.firstJoinDateEdt.text = DateUtils.getDDMMMYYYYFrom(mJoinDateSelected)
            _binding.contentForm2Icl.saveCreateBtn.visibility = View.GONE
            _binding.contentForm2Icl.saveUpdateBtn.visibility = View.VISIBLE
        }
    }

    private fun selectFirstJoinDate() {
        mDialogDatePicker?.let { dialogDatePicker ->
            if(!dialogDatePicker.isShowing){
                dialogDatePicker.show()
            }
        }
    }

    private fun setOnClickListener(context: Context) {
        _binding.contentForm2Icl.saveCreateBtn.setOnClickListener {
            val id = Integer.parseInt(_binding.contentForm2Icl.idEdt.text.toString())
            val nama = _binding.contentForm2Icl.firstnameEdt.text.toString()
            // val tglMasukKerja = _binding.contentForm2Icl.firstJoinDateEdt.text.toString()
            var usia = 0
            if(_binding.contentForm2Icl.firstAgeEdt.text.toString().isNotEmpty()){
                usia = Integer.parseInt(_binding.contentForm2Icl.firstAgeEdt.text.toString())
            }
            if (nama.isEmpty() || mJoinDateSelected == null ||
                _binding.contentForm2Icl.firstAgeEdt.text.toString().isEmpty()) {
                Toast.makeText(this, "Data tidak valid, silakan isi data dengan benar", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // val tglMasukKerjaFinal = DateUtils.getDateFrom("dd MMM yyyy", tglMasukKerja)
                addKaryawan(
                    context = context,
                    id = id,
                    nama = nama,
                    tglMasukKerja = mJoinDateSelected,
                    usia = usia
                )
            }
        }

        _binding.contentForm2Icl.saveUpdateBtn.setOnClickListener {
            val nama = _binding.contentForm2Icl.firstnameEdt.text.toString()
            // val tglMasukKerja = _binding.contentForm2Icl.firstJoinDateEdt.text.toString()
            val usia = Integer.parseInt(_binding.contentForm2Icl.firstAgeEdt.text.toString())
            if (nama.isEmpty() || mJoinDateSelected == null ||
                _binding.contentForm2Icl.firstAgeEdt.text.toString().isEmpty()) {
                Toast.makeText(this, "Data tidak valid, silakan isi data dengan benar", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // val tglMasukKerjaFinal = DateUtils.getDateFrom("dd MMM yyyy", tglMasukKerja)
                updateKaryawan(
                    context = context,
                    id = mEmployeeModel?.idKaryawan ?: -1,
                    nama = nama,
                    tglMasukKerja = mJoinDateSelected,
                    usia = usia
                )
            }
        }

        _binding.contentForm2Icl.firstJoinDateEdt.setOnClickListener {
            selectFirstJoinDate()
        }
    }

    private fun addKaryawan(context: Context, id: Int, nama: String, tglMasukKerja: Date?, usia: Int){
        dbHandler.newKaryawan(
            id = id,
            nama = nama,
            tglMasukKerja = tglMasukKerja,
            usia = usia,
            object: DBManagerListener.Create {
                override fun onSuccess() {
                    Toast.makeText(context, "Data karyawan berhasil dibuat", Toast.LENGTH_SHORT)
                        .show()
                    sendResult()
                }
            }
        )
    }

    private fun updateKaryawan(context: Context, id: Int, nama: String, tglMasukKerja: Date?, usia: Int){
        dbHandler.updateKaryawan(
            id = id,
            nama = nama,
            tglMasukKerja = tglMasukKerja,
            usia = usia,
            object: DBManagerListener.Update {
                override fun onSuccess() {
                    Toast.makeText(context, "Data karyawan berhasil diubah", Toast.LENGTH_SHORT)
                        .show()
                    sendResult()
                }
            }
        )
    }

    private fun sendResult(){
        val intent = intent
        intent.putExtra(ON_REFRESH, true)
        setResult(RESULT_OK, intent)
        finish()
    }
}