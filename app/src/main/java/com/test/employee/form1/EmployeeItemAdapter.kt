
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.employee.DateUtils
import com.test.employee.R

interface EmployeeItemAdapterListener {
    fun onClickItem(karyawan: EmployeeModel)
    fun itemsHasRefreshed()
}

class EmployeeItemAdapter(
    context: Context
) : RecyclerView.Adapter<EmployeeItemAdapter.ViewHolder>() {

    private val TAG = this::class.java.simpleName

    private var items = mutableListOf<EmployeeModel>()
    private var filteredItems = mutableListOf<EmployeeModel>()

    var listener: EmployeeItemAdapterListener? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<EmployeeModel>): EmployeeItemAdapter {
        this.items.clear()
        this.items.addAll(items)
        this.filteredItems.clear()
        this.filteredItems.addAll(items)
        this.listener?.itemsHasRefreshed()
        this.notifyDataSetChanged()
        return this
    }

    fun setListener(listener: EmployeeItemAdapterListener): EmployeeItemAdapter {
        this.listener = listener
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.employee_item, parent, false)
        )
    }

    override fun getItemCount() = filteredItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(filteredItems[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var tvID: TextView
        private lateinit var tvName: TextView
        private lateinit var tvTglMasukKerja: TextView
        private lateinit var tvUsia: TextView

        @SuppressLint("SetTextI18n") fun bindView(item: EmployeeModel) {
            tvID = itemView.findViewById(R.id.id_tv)
            tvName = itemView.findViewById(R.id.name_tv)
            tvTglMasukKerja = itemView.findViewById(R.id.tgl_masuk_kerja_tv)
            tvUsia = itemView.findViewById(R.id.usia_tv)

            tvID.text = item.idKaryawan.toString()
            tvName.text = item.namaKaryawan
            tvTglMasukKerja.text = DateUtils.getDDMMMYYYYFrom(item.tglMasukKerja)
            tvUsia.text = item.usia.toString()

            itemView.setOnClickListener {
                listener?.onClickItem(item)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun excuteFilter(searchText: String) {
        filteredItems = items
        if (searchText.isNotEmpty()) {
            val resultList = mutableListOf<EmployeeModel>()
            for (item in items) {
                if (item.namaKaryawan.lowercase().contains(searchText.lowercase())) {
                    resultList.add(item)
                }
            }
            filteredItems = resultList
        }
        this.notifyDataSetChanged()
    }
}