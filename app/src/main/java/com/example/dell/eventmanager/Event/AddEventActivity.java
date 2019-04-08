package com.example.dell.eventmanager.Event;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.eventmanager.MainActivity;
import com.example.dell.eventmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private ArrayList<Events> event_list = new ArrayList<Events>();
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    EditText addEventDetailEditText;
    Calendar myCalendar;

    ImageView backButton;
    TextView toolbarName;
    Button cancleButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_event_activity);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#FF6961"));
        }

        toolbarName = findViewById(R.id.toolbar_name);
        toolbarName.setText("Add Event");
        backButton = findViewById(R.id.back_button_dsc);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddEventActivity.this, MainActivity.class));
            }
        });


        final EditText addEventNameEditText = findViewById(R.id.add_event_name);
        addEventDetailEditText = findViewById(R.id.add_event_detail);

        cancleButton = findViewById(R.id.btn_cancel_add_event);
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        addEventDetailEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddEventActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        Button btnAddEvent = findViewById(R.id.btn_add_event_to_firebase);
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String addEventName = addEventNameEditText.getText().toString();
                final String addEventDetail = addEventDetailEditText.getText().toString();
                if (addEventName.isEmpty()) {
                    Toast.makeText(AddEventActivity.this, "Enter name for the event", Toast.LENGTH_SHORT).show();
                } else if (addEventDetail.isEmpty()) {
                    Toast.makeText(AddEventActivity.this, "Enter date for the event", Toast.LENGTH_SHORT).show();
                }
                else{

                String key = mMessagesDatabaseReference.child("toDoList").push().getKey();


                final Events todo = new Events();
                todo.setEventName(addEventName);
                todo.setEventDate(addEventDetail);
                todo.setEventKey(key);
                todo.setEventComplete("false");

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(key, todo.toEventsFirebaseObject());
                mMessagesDatabaseReference.child("toDoList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(AddEventActivity.this, "New Event Added", Toast.LENGTH_SHORT).show();
                            Intent intentToEventsActivity = new Intent(AddEventActivity.this, EventsActivity.class);
                            startActivity(intentToEventsActivity);
                        }
                    }
                });
            }
            }
        });


    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        addEventDetailEditText.setText(sdf.format(myCalendar.getTime()));
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //TODO
            return true;
        }
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
