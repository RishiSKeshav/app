package com.rishi.app.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by amitrajula on 2/20/16.
 */
public class EmailAuthentication extends AppCompatActivity {
    String EMAILID,CODE,ACTION;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_authentication);

        Toolbar toolbar= (Toolbar) findViewById(R.id.email_authentication_toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        CODE = i.getStringExtra("code");
        EMAILID = i.getStringExtra("emailId");

        Log.i("CODE",CODE);

        if(i.hasExtra("action")){
            ACTION = "reset_password";
        }


        TextView tv = (TextView)findViewById(R.id.text_code);
        tv.setText("You should have received a email with activation code. It was sent to " + EMAILID);

        et = (EditText) findViewById(R.id.code);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(CODE.equals(et.getText().toString())){

                    if(ACTION.equals("")) {

                        Intent i = new Intent(EmailAuthentication.this, RegistrationDetails.class);
                        i.putExtra("emailId", EMAILID);
                        startActivity(i);
                    }else{
                        Intent i = new Intent(EmailAuthentication.this, PasswordReset.class);
                        i.putExtra("emailId", EMAILID);
                        startActivity(i);
                    }


                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }
}
