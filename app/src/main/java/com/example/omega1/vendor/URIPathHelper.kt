import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore

class MediaUtils {
    companion object{
        fun getRealPathFromURI_API19(context: Context, uri: Uri?): String? {
            var filePath = ""
            val wholeID = DocumentsContract.getDocumentId(uri)
            val id = wholeID.split(":").toTypedArray()[1]
            val column = arrayOf(MediaStore.Images.Media.DATA)

            // where id is equal to
            val sel = MediaStore.Images.Media._ID + "=?"
            val cursor: Cursor? = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null
            )
            val columnIndex: Int = cursor!!.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
            return filePath
        }

        fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
            var cursor: Cursor? = null
            return try {
                val proj =
                    arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
                cursor!!.moveToFirst()
                val column_index = cursor.getColumnIndex(proj[0])

                cursor.getString(column_index)
            } finally {
                cursor?.close()
            }
        }

    }
}