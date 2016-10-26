package example.wangnan.com.nativepatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import example.wangnan.com.nativepatch.Utils.SoPatchUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    //     Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("mynativehelper");
    }

    private TextView mTextView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        mTextView = (TextView) findViewById(R.id.sample_text);
        mTextView.setText(firstStringFromJNI());

        mButton = (Button) findViewById(R.id.my_button);
        mButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        try {
            boolean closeLibraryResult = closeLibrary();   //卸载原来的s

            Log.d(TAG, "closeLibraryResult = " + closeLibraryResult);


            SoPatchUtils.doPatch(getApplicationContext());

            System.loadLibrary("native-lib");

            String str = firstStringFromJNI();
            mTextView.setText(str);
            Log.d(TAG, "str = " + str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String firstStringFromJNI();

    public native boolean closeLibrary();
//
//    public native String secondStringFromJNI();
}
