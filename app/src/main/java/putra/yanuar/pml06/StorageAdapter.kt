package putra.yanuar.pml06

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import coil.load

class StorageAdapter(context: Context, private val item: List<StorageItem>)
    : ArrayAdapter<StorageItem>(context, 0, item){
    //mengatur tampilan setiap baris data pada listview
    override fun getView(position: Int, convertView: View?, parent: ViewGroup)
            : View {
        var view = convertView
        //membuat tampilan baris menggunakan layout rowdata_storage
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout
                .rowdata_storage, parent, false)
        }
        val item = getItem(position)
        //menghubungkan komponen pada layout
        val icon = view!!.findViewById<ImageView>(R.id.imv)
        val fileNameTextView = view.findViewById<TextView>(R.id.txvFilename)
        val fileTypeTextView = view.findViewById<TextView>(R.id.txvFiletype)
        val fileUrlTextView = view.findViewById<TextView>(R.id.txvFileurl)
        //menampilkan nama file dan url
        fileNameTextView.text =item?.name
        fileUrlTextView.text = "URL:${item?.publicURL}"
        if(item!=null){
            val lowerCaseName = item.name.lowercase()
            //tampilan berdasarkan ekstensi file
            if(lowerCaseName.endsWith(".jpg") || lowerCaseName
                    .endsWith(".png")) {
                fileTypeTextView.text = "Tipe: Gambar"
                //menampilkan gambar dari url menggunakan coil
                icon.load(item.publicURL){
                    placeholder(R.drawable.loading)
                    error(R.drawable.error)
                }
            }
            else if (lowerCaseName.endsWith(".mp4") || lowerCaseName
                    .endsWith(".mkv")){
                fileTypeTextView.text = "Tipe: Video"
                icon.setImageResource(R.drawable.video)
            }
            else if (lowerCaseName.endsWith(".doc") || lowerCaseName
                    .endsWith(".docx")){
                fileTypeTextView.text = "Tipe: Dokumen"
                icon.setImageResource(R.drawable.dokumen)
            }
            else {
                fileTypeTextView.text = "Tipe: File"
                icon.setImageResource(R.drawable.dokumen)
            }
        } else {
            //tampilan jika data file tidak tersedia
            fileNameTextView.text = ""
            fileTypeTextView.text = "Tipe: Tidak Diketahui"
            icon.setImageResource(R.drawable.ic_launcher_foreground)
        }
        return view
    }
}