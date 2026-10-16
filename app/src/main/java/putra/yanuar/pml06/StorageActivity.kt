package putra.yanuar.pml06

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import putra.yanuar.pml06.databinding.ActivityStorageBinding

class StorageActivity : AppCompatActivity() {
    //tag untuk log
    private val TAG = "StorageActivity"
    private val BUCKET_NAME = "Mobile Lanjut"
    //koneksi supabase storage
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://ajsxopttanccdqzeupks.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFqc3hvcHR0YW5jY2RxemV1cGtzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTE0NjQ0NDIsImV4cCI6MjEwNzA0MDQ0Mn0.9tvihwWLTcJ8egHPPb6T7VfR36moUKAYX4kWDYOpaMo"
    ){
        install(Storage)
    }
    private lateinit var binding: ActivityStorageBinding
    private lateinit var storageAdapter : StorageAdapter
    //menyimpan uri (Uniform Resource Identifier) file yang dipilih
    private var selectedFileUri : Uri? = null
    //membuka file picker untuk memilih file dari perangkat
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()){ uri:Uri? ->
        uri?.let {
            selectedFileUri = it
            binding.txvSelectedFile.text = "File terpilih: " +
                    "${getFileName(it)}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.txvSelectedFile.text = "Belum ada File dipilih"
        //adapter untuk listview
        storageAdapter = StorageAdapter(this, emptyList())
        binding.lsvStorage.adapter = storageAdapter
        //mengatur tombol dan mengambil data dari storage
        setupButtons()
        fetchFilesFromStorage()
    }
    private fun setupButtons(){
        binding.btnimg.setOnClickListener {pickFile("image/*")}
        binding.btnvideo.setOnClickListener {pickFile("video/*") }
        binding.btnpdf.setOnClickListener {pickFile("application/pdf")}
        binding.btnword.setOnClickListener {
            pickFile("application/vnd.openxmlformats-officedocument" +
                    ".wordprocessingml.document")
        }
        binding.btnupload.setOnClickListener { uploadFile() }
    }
    //file picker sesuai tipe file
    private fun pickFile(type: String){
        filePickerLauncher.launch(type)
    }
    //mengunggah file yang telah dipilih ke supabase
    private fun uploadFile(){
        selectedFileUri?.let { uri ->
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val fileName = getFileName(uri)
                    val fileBytes = contentResolver
                        .openInputStream(uri)?.readBytes()
                    if (fileBytes != null) {
                        //mengunggah file ke bucket supabase
                        supabase.storage[BUCKET_NAME].upload(fileName,
                            fileBytes, upsert = true)
                        //kembali ke main thread untuk memperbarui tampilan
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@StorageActivity,
                                "Upload berhasil!", Toast.LENGTH_SHORT).show()
                            selectedFileUri = null
                            binding.txvSelectedFile.text =
                                "Belum ada file yang dipilih"
                            //memperbarui daftar file setelah upload
                            fetchFilesFromStorage()
                        }
                    }
                } catch (e: Exception) {
                    //mencatat kesalahan upload ke log
                    Log.e(TAG, "Upload file gagal, error:", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@StorageActivity,
                            "Upload gagal: ${e.message}",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        } ?: run {
            Toast.makeText(this, "Pilih file terlebih dahulu",
                Toast.LENGTH_SHORT).show()
        }
    }

    //mengambil daftar file dari supabase storage
    private fun fetchFilesFromStorage() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val fileObjects = supabase.storage[BUCKET_NAME].list()
                //abaikan placeholder dan ubah data menjadi storageitem
                val storageItems = fileObjects.filter {
                    it.name != ".emptyFolderPlaceholder" }.map { fileObject ->
                    val publicUrl = supabase.storage[BUCKET_NAME]
                        .publicUrl(fileObject.name)
                    StorageItem(
                        name = fileObject.name,
                        mimeType = fileObject.metadata
                            ?.get("mimetype") as? String,
                        publicURL = publicUrl
                    )
                }
                //memperbarui listview pada main thread
                withContext(Dispatchers.Main) {
                    storageAdapter = StorageAdapter(this@StorageActivity,
                        storageItems)
                    binding.lsvStorage.adapter = storageAdapter
                    Toast.makeText(this@StorageActivity,
                        "Data berhasil dimuat", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                //menampilkan pesan jika gagal mengambil data
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@StorageActivity,
                        "Gagal memuat data: ${e.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //mengambil nama file berdasarkan uri
    private fun getFileName(uri: Uri): String {
        var name = "unknown"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }
}