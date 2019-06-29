package com.journaldev.barcodevisionapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    com.beardedhen.androidbootstrap.BootstrapButton btnTakePicture, btnScanBarcode;
    String[] country = {"USD", "KWD", "MOP", "JPD", "TWD"};
    String[] rateStringList = {"8.2", "10.2", "5.2", "7.4", "4.2"};
    TextView rateTextView;
    TextView totalTextView;
    Spinner spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    EditText inputAmount;
    com.beardedhen.androidbootstrap.BootstrapButton confirmButton;

    private void initViews() {
        btnTakePicture = findViewById(R.id.btnTakePicture);
        btnScanBarcode = findViewById(R.id.btnScanBarcode);


        inputAmount = findViewById(R.id.editText);
        rateTextView = findViewById(R.id.rate);
        totalTextView = findViewById(R.id.total);
        confirmButton = findViewById(R.id.confirm);


        inputAmount.getText();
        inputAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!inputAmount.getText().toString().matches("")) {
                    BigDecimal inputAmount_c = new BigDecimal(inputAmount.getText().toString());
                    BigDecimal rate_c = new BigDecimal(rateStringList[currencySelection]);
                    BigDecimal total_c = inputAmount_c.multiply(rate_c);
                    totalTextView.setText(total_c.toString());
                }

            }
        });
        btnTakePicture.setOnClickListener(this);
        btnScanBarcode.setOnClickListener(this);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RewardActivity.class);
                intent.putExtra("total",totalTextView.getText().toString());
                finalMsg = spin.getSelectedItem().toString()+" "+inputAmount.getText();

                startActivity(intent);
            }
        });
        spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        spin.setSelection(0);
    }

    static public String finalMsg="";

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnTakePicture:
                startActivity(new Intent(MainActivity.this, PictureBarcodeActivity.class));
                break;
            case R.id.btnScanBarcode:
                startActivity(new Intent(MainActivity.this, ScannedBarcodeActivity.class));
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        rateTextView.setText(rateStringList[i]);
        currencySelection = i;

        if (!inputAmount.getText().toString().matches("")) {
            BigDecimal inputAmount_c = new BigDecimal(inputAmount.getText().toString());
            BigDecimal rate_c = new BigDecimal(rateStringList[currencySelection]);
            BigDecimal total_c = inputAmount_c.multiply(rate_c);
            totalTextView.setText(total_c.toString());
        }

    }

    int currencySelection;

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
