package com.codesmugglers.booknerd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private static String TAG = "RegistrationActivity";

    private Button mRegister;
    private EditText mEmail, mPassword, mName, mCity;
    private RadioGroup mGender;
    private TextView mDateTextView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // If user already logged in, skip this activity
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        /* Current date in the birthday field */
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        String currentDate = sdf.format(calendar.getTime());
        mDateTextView = findViewById(R.id.date_of_birth);
        mDateTextView.setText(currentDate);
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                mDateTextView.setText(date);
            }
        };

        mRegister = findViewById(R.id.register);

        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mCity = findViewById(R.id.city);
        mGender = findViewById(R.id.gender);

        mRegister.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();

                final RadioButton gender = findViewById(mGender.getCheckedRadioButtonId());

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            Map<String,String> userData = new HashMap<String,String>();
                            userData.put("Name", mName.getText().toString());
                            userData.put("City", mCity.getText().toString());
                            userData.put("DOB", mDateTextView.getText().toString());
                            userData.put("Gender", gender.getText().toString());

                            userDb.setValue(userData);
                        }
                        else{
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void onClickLogIn(View view) {
        Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(firebaseAuthStateListener != null){
            mAuth.removeAuthStateListener(firebaseAuthStateListener);
        }
    }

    public void onClickDateOfBirth(View view) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(
                RegistrationActivity.this,
                android.R.style.Theme,
                mDateSetListener,
                year,month,day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }
}
