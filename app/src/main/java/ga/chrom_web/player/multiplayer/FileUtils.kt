package ga.chrom_web.player.multiplayer

import java.io.*

class FileUtils {
    companion object {

        @JvmStatic
        public fun write(file: File, data: String) {
            val fos: FileOutputStream
            var oos: ObjectOutputStream? = null
            try {
                fos = FileOutputStream(file)
                oos = ObjectOutputStream(fos)
                oos.writeObject(data)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    oos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        @JvmStatic
        public fun read(file: File): String {
            var fis: FileInputStream? = null
            var ois: ObjectInputStream? = null
            var data: String? = null
            try {
                fis = FileInputStream(file)
                ois = ObjectInputStream(fis)
                data = ois.readObject() as String?
            } catch (e: FileNotFoundException) {
                //file not created yet
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    ois?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return if (data != null) data else ""
        }
    }
}