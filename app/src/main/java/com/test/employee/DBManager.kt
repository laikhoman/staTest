package com.test.employee

import EmployeeModel
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.google.gson.Gson
import com.test.employee.DateUtils
import java.util.Date

interface DBManagerListener {
    interface Create {
        fun onSuccess()
    }
    interface Update {
        fun onSuccess()
    }
    interface Delete {
        fun onSuccess()
    }
    interface GetItems {
        fun onSuccess(items: List<EmployeeModel>)
    }
}

class DBManager  // creating a constructor for our database handler.
    (context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    private val TAG = this::class.java.simpleName

    companion object {
        // creating a constant variables for our database.
        // below variable is for our database name.
        private const val DB_NAME = "KaryawanDB"

        // below int is our database version
        private const val DB_VERSION = 1

        // below variable is for our table name.
        private const val TABLE_NAME = "karyawan"

        // below variable is for our id column.
        private const val COL_ID_KARYAWAN = "idKaryawan"

        // below variable is for our course name column
        private const val COL_NAMA_KARYAWAN = "namaKaryawan"

        // below variable id for our course duration column.
        private const val COL_TGL_MASUK_KERJA = "tglMasukKerja"

        // below variable for our course description column.
        private const val COL_USIA = "usia"
    }

    // below method is for creating a database by running a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID_KARYAWAN + " TEXT, "
                + COL_NAMA_KARYAWAN + " TEXT,"
                + COL_TGL_MASUK_KERJA + " DATETIME,"
                + COL_USIA + " INTEGER)")

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query)
    }

    // this method is use to add new course to our sqlite database.
    fun newKaryawan(
        id: Int?,
        nama: String?,
        tglMasukKerja: Date?,
        usia: Int?,
        listener: DBManagerListener.Create?
    ) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        val db = this.writableDatabase

        // on below line we are creating a
        // variable for content values.
        val values = ContentValues()

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(COL_ID_KARYAWAN, id)
        values.put(COL_NAMA_KARYAWAN, nama)
        values.put(COL_TGL_MASUK_KERJA, DateUtils.getYYYYMMDDHHMMSS(tglMasukKerja))
        values.put(COL_USIA, usia)
        Log.d(TAG, "values: ${Gson().toJson(values)}")

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values)

        // at last we are closing our
        // database after adding database.
        db.close()
        listener?.onSuccess()
    }

    // this method is use to add new course to our sqlite database.
    fun updateKaryawan(
        id: Int?,
        nama: String?,
        tglMasukKerja: Date?,
        usia: Int?,
        listener: DBManagerListener.Update?
    ) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        val db = this.writableDatabase

        // on below line we are creating a
        // variable for content values.
        val values = ContentValues()

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(COL_ID_KARYAWAN, id)
        values.put(COL_NAMA_KARYAWAN, nama)
        values.put(COL_TGL_MASUK_KERJA, DateUtils.getYYYYMMDDHHMMSS(tglMasukKerja))
        values.put(COL_USIA, usia)
        Log.d(TAG, "values: ${Gson().toJson(values)}")

        // after adding all values we are passing
        // content values to our table.
        db.update(TABLE_NAME, values, "$COL_ID_KARYAWAN = $id", null)

        // at last we are closing our
        // database after adding database.
        db.close()
        listener?.onSuccess()
    }

    @SuppressLint("Range") fun getItems(
        listener: DBManagerListener.GetItems?
    ) {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COL_NAMA_KARYAWAN ASC"
        Log.d(TAG, "query: $query")
        val cursor: Cursor = db.rawQuery(query, null)

        val items = mutableListOf<EmployeeModel>()
        while (cursor.moveToNext()) {
            items.add(EmployeeModel(cursor))
        }
        cursor.close()
        db.close()
        listener?.onSuccess(items)
    }

    @SuppressLint("Range") fun getItems(
        firstName: String,
        lastName: String,
        startDate: Date?,
        endDate: Date?,
        firstUsia: Int,
        lastUsia: Int,
        listener: DBManagerListener.GetItems?
    ) {
        val db = this.readableDatabase

        val startDt = DateUtils.getYYYYMMDDHHMMSS(startDate)
        val endDt = DateUtils.getYYYYMMDDHHMMSS(endDate)

        var WHERE_QUERY = "WHERE $COL_USIA >= $firstUsia AND $COL_USIA <= $lastUsia"

        if(firstName.isNotEmpty() && lastName.isNotEmpty()){
            WHERE_QUERY = "$WHERE_QUERY " +
                    " AND $COL_NAMA_KARYAWAN LIKE '%$firstName%' OR $COL_NAMA_KARYAWAN LIKE '%$lastName%'"
        } else {
            if (firstName.isNotEmpty() && lastName.isEmpty()) {
                WHERE_QUERY = "$WHERE_QUERY " +
                        " AND $COL_NAMA_KARYAWAN LIKE '%$firstName%'"
            } else if (firstName.isEmpty() && lastName.isNotEmpty()) {
                WHERE_QUERY = "$WHERE_QUERY " +
                        " AND $COL_NAMA_KARYAWAN LIKE '%$lastName%'"
            }
        }

        if(startDate != null && endDate != null){
            WHERE_QUERY = "$WHERE_QUERY " +
                    " AND $COL_TGL_MASUK_KERJA >= '$startDt' AND $COL_TGL_MASUK_KERJA <= '$endDt'"
        } else {
            if(startDate != null && endDate == null){
                WHERE_QUERY = "$WHERE_QUERY " +
                        " AND $COL_TGL_MASUK_KERJA = '$startDt'"
            } else if(startDate == null && endDate != null){
                WHERE_QUERY = "$WHERE_QUERY " +
                        " AND $COL_TGL_MASUK_KERJA = '$endDate'"
            }
        }

        val query = "SELECT * FROM $TABLE_NAME $WHERE_QUERY ORDER BY $COL_NAMA_KARYAWAN ASC"
        Log.d(TAG, "query: $query")
        val cursor: Cursor = db.rawQuery(query, null)

        val items = mutableListOf<EmployeeModel>()
        while (cursor.moveToNext()) {
            items.add(EmployeeModel(cursor))
        }
        cursor.close()
        db.close()
        listener?.onSuccess(items)
    }


    @SuppressLint("Range") fun deleteKaryawan(
        id: Int,
        listener: DBManagerListener.Delete?
    ) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_ID_KARYAWAN = $id", null)
        db.close()
        listener?.onSuccess()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }
}
