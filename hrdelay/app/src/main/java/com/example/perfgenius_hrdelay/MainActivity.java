package com.example.perfgenius_hrdelay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText edIter = findViewById(R.id.delay);
        final TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        // Example of a call to a native method
        Button button = findViewById(R.id.example);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = edIter.getText().toString();
                str = str.replaceAll("^[.]", "0.");
                Float delay_s = Float.parseFloat("".equals(str.toString())?"0":str.toString());
                tv.setText(numFromJNI(delay_s));
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public native String numFromJNI(float delay_s);
}
