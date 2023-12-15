
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import java.util.Calendar

@SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
class DialogDatePicker(
    context: Context, calendar: Calendar, listener: OnDateSetListener
) : DatePickerDialog(
    context, AlertDialog.THEME_HOLO_LIGHT, listener, calendar.get(Calendar.YEAR),
    calendar.get(Calendar.MONTH),
    calendar.get(Calendar.DAY_OF_MONTH)
) {

    private val TAG = DialogDatePicker::class.java.simpleName

    init {
        Log.d(
            TAG, "YEAR: ${calendar.get(Calendar.YEAR)} " +
                    "MONTH: ${calendar.get(Calendar.MONTH)} " +
                    "DAY_OF_MONTH: ${calendar.get(Calendar.DAY_OF_MONTH)}"
        )
        this.setCanceledOnTouchOutside(true)
        this.setCancelable(true)
    }
}