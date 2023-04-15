import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore


/**
 * A group of *vendor*.
 *
 * Object used as uri to file converter.
 *
 * originally from Handy Opinion
 * https://handyopinion.com/get-path-from-uri-in-kotlin-android/
 */
object UriToFileConvertor {
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
    /**
     * A group of *vendor_function*.
     *
     * Function used to get real path from uri
     *
     * @param context context of activity or fragment where is function called
     * @param contentUri uri which real path is going to be crated
     * @return string real path that can be used as parameter into File object
     */
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