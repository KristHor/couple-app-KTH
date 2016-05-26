package com.couple.kristjanthor.appforcouple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

    }

   public void  StartBtn(View view) {

       final EditText editName = (EditText) findViewById(R.id.editTextName);
       String userName = editName.getText().toString();

       if(userName == null || userName.trim().equals("")){
           Toast.makeText(this, "Sorry you did't type anything", Toast.LENGTH_SHORT).show();
       }

       else

           try {
               Intent main = new Intent(LogIn.this, MainActivity.class);
               main.putExtra("userName", userName);
               startActivity(main);

               }catch(Exception e){
                   Toast.makeText(getApplicationContext(), "Error, try agian", Toast.LENGTH_LONG).show();
               }

           }
   }


