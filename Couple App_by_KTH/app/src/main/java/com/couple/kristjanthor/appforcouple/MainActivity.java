package com.couple.kristjanthor.appforcouple;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "Camera01";
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private enum MediaType {IMAGE}
    private DAO dao;
    private Firebase mRef;
    Button sendBtn, btnDelete;
    EditText txtInput;
    ImageView imageProfile;
    File m_file;
    TextView uName;
    ListView coupleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        Intent ServiceIntent = new Intent(this, Notification.class);
        startService(ServiceIntent);

        dao = new DAO(this);


        sendBtn = (Button) findViewById(R.id.buttonSend);
        btnDelete = (Button) findViewById(R.id.button2);
        txtInput = (EditText) findViewById(R.id.editText);
        uName = (TextView) findViewById(R.id.textViewName);

        getName();

        clearValue();
        showMessages(dao.selectAll());

    }

    public void isequel() {

        String outT = Messages.getInstance().getString();
        String inT = Messages.getInstance().getInput();

        if (!outT.equals(inT)) {

            Intent intent = new Intent("event");
            intent.putExtra("InputMessage", inT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    public void getName(){

    Bundle nameData = getIntent().getExtras();
    if (nameData == null) {
        return;
    }
    String userName = nameData.getString("userName");
    uName.setText("Hi  " + userName + "!");
}
    public void startCamera(View view){

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    m_file = getOutputMediaFile(MediaType.IMAGE); // create a file to save the image
    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(m_file)); // set the image file name

    log("file uri = " + Uri.fromFile(m_file).toString());

    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
}

    @Override
    public void onStart() {
        super.onStart();

        try {
            mRef = new Firebase("https://coupleapp.firebaseio.com/");
        } catch (Exception Ex) {
            Log.d(LOGTAG, "Connection error, firebase URL");
            Toast.makeText(this, "Error,no connection to firebase", Toast.LENGTH_LONG).show();
        }

//FromFireBase Value
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newTxt = (String) dataSnapshot.getValue();
                Object nexTxt = 0;

                Messages.getInstance().setInput(newTxt);

              try {

                if (newTxt != null) {
                    dao.insert(new BECouple(0, newTxt));
                    showMessages(dao.selectAll());

                    isequel();

                } else if (nexTxt == null) {
                    showMessages(dao.selectAll());
                }}
                    catch (Exception Ex)
                    {
                        Log.d(LOGTAG, "Value null");
                        Toast.makeText(MainActivity.this, "You have no messages in your database",Toast.LENGTH_LONG).show();
                    }
                }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void onClickSend(View view) {
        //ToFireBase...
        try{
        Bundle nameData = getIntent().getExtras();
        if (nameData == null) {
            return;
        }
        String userName = nameData.getString("userName");
        String myTxt = txtInput.getText().toString();
        mRef.setValue(userName + ": " + myTxt);
            String txtToNot = (userName + ": " + myTxt);
            Messages.getInstance().setString(txtToNot);
            clearValue();
        } catch (Exception e) {
            Toast.makeText(this, "Error, send message",Toast.LENGTH_LONG).show();
        }
    }

    public void onClickDeleteAll(View view) {
        try {
            dao.deleteAll();
            showMessages(dao.selectAll());

        } catch (Exception e) {
            Toast.makeText(this,"Error, could not delete list from db",Toast.LENGTH_LONG).show();
        }
    }


    void showMessages(List<BECouple> couple) {
        try {
        StringBuilder sb = new StringBuilder();
        for (BECouple p : couple)
            sb.append(p.toString() + "\n");
        String[] temp = sb.toString().split("TABTAB");

        coupleList = (ListView) findViewById(R.id.listViewMes);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, temp);
        coupleList.setAdapter(adapter);
        clearValue();
        } catch (Exception e) {
            Toast.makeText(this, "Error, could not get to messages from db",Toast.LENGTH_LONG).show();
        }
    }

    private File getOutputMediaFile(MediaType mediaType) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera01");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Toast.makeText(this, "failed to create directory", Toast.LENGTH_LONG).show();
                log("failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String postfix = mediaType == MediaType.IMAGE ? "jpg" : "mp4";
        String prefix = mediaType == MediaType.IMAGE ? "IMG" : "VID";

        File mediaFile = new File(mediaStorageDir.getPath() +
                File.separator + prefix +
                "_" + timeStamp + "." + postfix);

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                String filename = m_file.toString();
                showPictureTaken(filename);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled...", Toast.LENGTH_LONG).show();
                return;

            } else
                Toast.makeText(this, "Picture NOT taken - unknown error...", Toast.LENGTH_LONG).show();
        }
    }

    private void showPictureTaken(String filename) {
        try{
        imageProfile = (ImageView) findViewById(R.id.imageProfile);
        File f = new File(filename);
        imageProfile.setImageURI(Uri.fromFile(f));
        } catch (Exception e) {
            Toast.makeText(this, "Error, pitcure could not be shown",Toast.LENGTH_LONG).show();
        }
    }

    void log(String s) {
        Log.d(LOGTAG, s);
    }

    private void clearValue() {
        CountDownTimer c = new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {

                mRef.setValue(null);
            }

        }.start();
    }

}
