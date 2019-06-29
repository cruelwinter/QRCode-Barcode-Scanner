package com.journaldev.barcodevisionapi;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    com.beardedhen.androidbootstrap.BootstrapButton btnTakePicture, btnScanBarcode;
    String[] country;
    //    String[] rateStringList = {"8.2", "10.2", "5.2", "7.4", "4.2"};
    ArrayList<CountryCode> currencyRateList_api = new ArrayList();
    TextView rateTextView;
    TextView totalTextView;
    Spinner spin;

    public class CountryCode {
        String id;
        String currencyCode;
        String rate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String rateList = RewardActivity.getHtmlByGet("http://192.168.37.105:8080/demo/rate/get-all");
        try {
            JSONArray jsonArray = new JSONArray(rateList);

//            JSONArray jsonArray = jsnobject.getJSONArray(rateList);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                CountryCode countryCode = new CountryCode();
                countryCode.id = explrObject.get("id").toString();
                countryCode.currencyCode = explrObject.get("currencyCode").toString();
//                countryCode.currencyCode = countryCode.currencyCode.replace("//HKD", "");
                countryCode.rate = explrObject.get("rate").toString();
                currencyRateList_api.add(countryCode);
            }

            country = new String[currencyRateList_api.size()];


            System.out.println("print obj");
            for (int i = 0; i < currencyRateList_api.size(); i++) {
                System.out.println(currencyRateList_api.get(i));
                System.out.println(currencyRateList_api.get(i).id);
                System.out.println(currencyRateList_api.get(i).currencyCode);
                System.out.println(currencyRateList_api.get(i).rate);
                country[i] = currencyRateList_api.get(i).currencyCode;
            }

            ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, country);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spin.setAdapter(aa);
            spin.setSelection(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("rateList: " + rateList);


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
                    BigDecimal rate_c = new BigDecimal(currencyRateList_api.get(currencySelection).rate);
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
                if (inputAmount.getText().equals("")||inputAmount.getText().equals(" ")
                        ||inputAmount.getText().toString().matches("")) {
                    return;
                }
                Intent intent = new Intent(MainActivity.this, RewardActivity.class);
                intent.putExtra("total", totalTextView.getText().toString());
                finalMsg = spin.getSelectedItem().toString() + " " + inputAmount.getText();
                // USD 88
                startActivity(intent);
            }
        });
        spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list


    }

    static public String finalMsg = "";
    static public String selectedGiftsString = "";


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnTakePicture:
                // show barCode
                startActivity(new Intent(MainActivity.this, PictureBarcodeActivity.class));
                break;
            case R.id.btnScanBarcode:
                // scan qr code
                startActivity(new Intent(MainActivity.this, ScannedBarcodeActivity.class));
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        rateTextView.setText(currencyRateList_api.get(i).rate);
        currencySelection = i;

        if (!inputAmount.getText().toString().matches("")) {
            BigDecimal inputAmount_c = new BigDecimal(inputAmount.getText().toString());
            BigDecimal rate_c = new BigDecimal(currencyRateList_api.get(i).rate);
            BigDecimal total_c = inputAmount_c.multiply(rate_c);
            totalTextView.setText(total_c.toString());
        }

    }

    int currencySelection;

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
