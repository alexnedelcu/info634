package info634.alexnedelcu.com.info634;

import android.content.Context;

import java.io.File;

/**
 * Created by Alex on 4/26/2016.
 */
public class IO {
    File mFile;


    public static void open(Context context) {
        File file = new File(context.getFilesDir(), "run");

    }

    public static void write (String row) {

    }
}
