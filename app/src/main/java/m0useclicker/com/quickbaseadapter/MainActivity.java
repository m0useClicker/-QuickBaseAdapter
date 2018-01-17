package m0useclicker.com.quickbaseadapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import m0useclicker.com.quickbaseadapter.qbActions.login.QbLogin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new QbLogin().execute("qwe","qwe","qwe");
    }
}
