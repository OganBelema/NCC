package com.compunetlimited.ogan.ncc.actvities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.compunetlimited.ogan.ncc.NetworkConnectivity;
import com.compunetlimited.ogan.ncc.R;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class Give extends AppCompatActivity {

    private EditText EmailAddressEditText;
    private EditText AmountEditText;
    private EditText CardNumberEditText;
    private EditText CardCVCEditText;
    private EditText ExpiryMonthEditText;
    private EditText ExpiryYearEditText;

    private Charge charge;

    private ProgressBar PaymentLoading;
    private LinearLayout CardDetailsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PaystackSdk.initialize(this);
        PaystackSdk.setPublicKey(getResources().getString(R.string.live_key));

        setContentView(R.layout.activity_give);

        EmailAddressEditText = findViewById(R.id.et_email_address);
        AmountEditText = findViewById(R.id.et_amount);
        CardNumberEditText = findViewById(R.id.et_card_number);
        CardCVCEditText = findViewById(R.id.et_card_cvc);
        ExpiryMonthEditText = findViewById(R.id.et_expiry_month);
        ExpiryYearEditText = findViewById(R.id.et_expiry_year);

        PaymentLoading = findViewById(R.id.payment_loading);
        CardDetailsLayout = findViewById(R.id.payment_card_layout);

        Button payButton = findViewById(R.id.btn_pay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkConnectivity.checkNetworkConnecttion(getApplicationContext())) {
                    paymentLoading();
                    pay();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


    }

    private int convertToKobo(int amount){
        return amount * 100;
    }

    private void pay(){

        boolean cancel = false;
        View focusView = null;

        String email = EmailAddressEditText.getText().toString().trim();
        String amountInNaira = AmountEditText.getText().toString().trim();
        String cardNumber = CardNumberEditText.getText().toString().trim();
        String cardCVC = CardCVCEditText.getText().toString().trim();
        String expiryMonth = ExpiryMonthEditText.getText().toString().trim();
        String expiryYear = ExpiryYearEditText.getText().toString().trim();

        if (!isEmailValid(email) || email.isEmpty()){
            EmailAddressEditText.setError(getString(R.string.error_field_required));
            focusView = EmailAddressEditText;
            cancel = true;
        }

        if (amountInNaira.isEmpty() || amountInNaira.equals("0")){
            AmountEditText.setError(getString(R.string.error_field_required));
            focusView = AmountEditText;
            cancel = true;
        }

        if (cardNumber.isEmpty()){
            CardNumberEditText.setError(getString(R.string.error_field_required));
            focusView = CardNumberEditText;
            cancel = true;
        }

        if (cardCVC.isEmpty()){
            CardCVCEditText.setError(getString(R.string.error_field_required));
            focusView = CardCVCEditText;
            cancel = true;
        }

        if (expiryMonth.isEmpty()){
            ExpiryMonthEditText.setError(getString(R.string.error_field_required));
            focusView = ExpiryMonthEditText;
            cancel = true;
        }

        if (expiryYear.isEmpty()){
            ExpiryYearEditText.setError(getString(R.string.error_field_required));
            focusView = ExpiryYearEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            Card card = new Card.Builder(cardNumber,
                    Integer.valueOf(expiryMonth),
                    Integer.valueOf(expiryYear),
                    cardCVC).build();

            int amountToBePayed = convertToKobo(Integer.valueOf(amountInNaira));

            if (card.isValid()){
                charge = new Charge();
                charge.setEmail(email);
                charge.setAmount(amountToBePayed);
                charge.setCard(card);
                performCharge();
            }

        }

    }

    private void performCharge(){
        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
                paymentLoaded();
                String reference = transaction.getReference();
                Toast.makeText(getApplicationContext(), "Your transaction was successful", Toast.LENGTH_LONG)
                        .show();
                System.out.println(transaction.getReference());
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here
                paymentLoaded();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG)
                        .show();
                System.err.println(error.getMessage());
            }

        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private void paymentLoading(){
        CardDetailsLayout.setVisibility(View.GONE);
        PaymentLoading.setVisibility(View.VISIBLE);
    }

    private void paymentLoaded(){
        emptyEditTexts();
        PaymentLoading.setVisibility(View.GONE);
        CardDetailsLayout.setVisibility(View.VISIBLE);
    }

    private void emptyEditTexts(){
        EmailAddressEditText.getText().clear();
        AmountEditText.getText().clear();
        CardNumberEditText.getText().clear();
        CardCVCEditText.getText().clear();
        ExpiryMonthEditText.getText().clear();
        ExpiryYearEditText.getText().clear();
    }

}