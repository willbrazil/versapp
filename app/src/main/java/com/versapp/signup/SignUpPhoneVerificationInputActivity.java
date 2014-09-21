package com.versapp.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.versapp.R;

import java.io.IOException;

public class SignUpPhoneVerificationInputActivity extends Activity {

    EditText phoneEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_phone_verification_input);

        phoneEdit = (EditText) findViewById(R.id.sign_up_phone_edit);

        String defaultPhone = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getLine1Number();

        if (defaultPhone.length() > 10) {
            defaultPhone = defaultPhone.replace("\\D+", "").replaceFirst("1", "");
        }

        phoneEdit.setText(defaultPhone);

        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                // Remove US area code.
                if (phoneEdit.getText().toString().length() > 10) {
                    phoneEdit.setText(phoneEdit.getText().toString().replace("^+1", ""));
                }
            }
        });

    }


    public void next(View view){

        boolean requireVerification = true;

        if (!requireVerification){

            startActivity(new Intent(SignUpPhoneVerificationInputActivity.this, SignUpUsernamePassInputActivity.class));

        }  else {


            // validate phone number.
            final String phone = phoneEdit.getText().toString();

            // send text message.
            if (phone.length() > 0) {

                RegistrationManager.getInstance(this).storePhone(phone);

                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {

                        try {

                            return RegistrationManager.getInstance(SignUpPhoneVerificationInputActivity.this).isPhoneAvailable(phone);

                        } catch (IOException e) {

                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Boolean isAvailable) {


                        if (isAvailable == null) {
                            Toast.makeText(SignUpPhoneVerificationInputActivity.this, "We are currently unable to verify your phone. Please try again soon.", Toast.LENGTH_LONG).show();
                        } else if (isAvailable) {

                            new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {

                                    RegistrationManager.getInstance(getApplicationContext()).sendConfirmationText();

                                    startActivity(new Intent(SignUpPhoneVerificationInputActivity.this, VerificationCodeInputActivity.class));

                                    return null;
                                }
                            }.execute();

                        } else {
                            Toast.makeText(SignUpPhoneVerificationInputActivity.this, "Number is already taken.", Toast.LENGTH_SHORT).show();
                        }


                        super.onPostExecute(isAvailable);
                    }
                }.execute();

            } else {

                Toast.makeText(this, "Invalid number.", Toast.LENGTH_SHORT).show();

            }

        }

    }


}