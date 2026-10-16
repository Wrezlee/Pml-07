package putra.yanuar.pml06

//menyimpan data informasi file yang terdapat di Supabase Storage
data class StorageItem (
    val name : String,
    val mimeType : String?,
    val publicURL : String
)