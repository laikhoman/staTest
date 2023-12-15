
import android.annotation.SuppressLint
import android.database.Cursor
import com.google.gson.annotations.SerializedName
import com.test.employee.DateUtils
import java.io.Serializable
import java.util.Date

data class EmployeeModel(

    @SerializedName("idKaryawan") var idKaryawan: Int?,
    @SerializedName("namaKaryawan") var namaKaryawan: String,
    @SerializedName("tglMasukKerja") var tglMasukKerja: Date?,
    @SerializedName("usia") var usia: Int

) : Serializable {
    constructor() : this(0, "", null, 0)

    @SuppressLint("Range") constructor(cursor: Cursor) : this(
        idKaryawan = cursor.getInt(cursor.getColumnIndex("idKaryawan")),
        namaKaryawan = cursor.getString(cursor.getColumnIndex("namaKaryawan")),
        tglMasukKerja = DateUtils.getDateFrom(
            "yyyy-MM-dd hh:mm:ss",
            cursor.getString(cursor.getColumnIndex("tglMasukKerja"))
        ),
        usia = cursor.getInt(cursor.getColumnIndex("usia"))
    )
}