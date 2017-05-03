package edu.utep.cs.cs4330.smartlock2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences mSettings= getSharedPreferences("Setting", Context.MODE_PRIVATE);
        final Button login = (Button)findViewById(R.id.loginButton);
        final EditText userName = (EditText) findViewById(R.id.userName);
        final EditText password = (EditText) findViewById(R.id.password);
        final CheckBox savePassword = (CheckBox)findViewById(R.id.savePassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(savePassword.isChecked()){
                    SharedPreferences mSettings= getSharedPreferences("Setting", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSettings.edit();
                    String savedUsername = userName.getText().toString();
                    String savedPassword = password.getText().toString();
                    editor.putString(savedUsername,savedPassword);
                    editor.apply();
                }
                if(userName.getText().toString() != null){
                    SharedPreferences mSettings= getSharedPreferences("Setting", Context.MODE_PRIVATE);
                    String retrivedPassword = mSettings.getString(userName.getText().toString(), "missing");
                    //if(password.getText().toString() == retrivedPassword){
                        Intent goToNextActivity = new Intent(getApplicationContext(), lockActivity.class);
                        startActivity(goToNextActivity);
                   // }
                }
            }
        });

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences mSettings= getSharedPreferences("Setting", Context.MODE_PRIVATE);
                String retrivedPassword = mSettings.getString(userName.getText().toString(), "missing");
                if(retrivedPassword.equals("missing")){

                }else{
                    password.setText(retrivedPassword);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}