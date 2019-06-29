package com.journaldev.barcodevisionapi;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;

public class RewardActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button btnOpenCamera;
    TextView txtResultBody;

    private BarcodeDetector detector;
    private Uri imageUri;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int CAMERA_REQUEST = 101;
    private static final String TAG = "API123";
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";

    String[] giftList = {"$10 starbucks", "$20 welcome coupon", "$30 marketplace coupon ", "$50 mangings coupon", "$100 7-11 coupon "};
    String[] topupMethod = {"Octopus", "Alipay", "wechat pay"};

    Button giftButtonV;
    Button topupButtonV;
    ListView mylistView;
    Adapter myAdapter;
    Context context = this;
    TextView selectedGiftTextView;
    TextView creditLeftTx;
    Button clearButton;

    ArrayList<String> selectedGifts = new ArrayList<>();

    BigDecimal creditTotal = new BigDecimal(0);
    BigDecimal creditTotalOri = new BigDecimal(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward);


        selectedGiftTextView = findViewById(R.id.selectedGiftTv);
        creditLeftTx = findViewById(R.id.creditLeft);
        clearButton = findViewById(R.id.clear);
        topupButtonV = findViewById(R.id.topupButton);


        topupButtonV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //set the title for alert dialog
                builder.setTitle("Choose way to top all the remaining money: ");

                //set items to alert dialog. i.e. our array , which will be shown as list view in alert dialog
                builder.setItems(topupMethod, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        selectedGiftTextView.setText(
                                selectedGiftTextView.getText()
                                        + "\n Top up " + creditTotal
                                        + " to " + topupMethod[item]);
                        creditTotal=new BigDecimal(0);
                        creditLeftTx.setText("you have credit left: " + creditTotal);


                    }
                });

                //Creating CANCEL button in alert dialog, to dismiss the dialog box when nothing is selected
                builder.setCancelable(false)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //When clicked on CANCEL button the dalog will be dismissed
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        String total = getIntent().getStringExtra("total");
        creditLeftTx.setText("you have credit left: " + total);
        creditTotal = new BigDecimal(total);
        creditTotalOri = new BigDecimal(total);

        giftButtonV = findViewById(R.id.giftButton);
        giftButtonV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                //set the title for alert dialog
                builder.setTitle("Choose names: ");

                //set items to alert dialog. i.e. our array , which will be shown as list view in alert dialog
                builder.setItems(giftList, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        //setting the button text to the selected itenm from the list
//                        button.setText(items[item]);

                        BigDecimal temp = creditTotal.subtract(new BigDecimal((item + 1) * 10));
                        if (temp.compareTo(BigDecimal.ZERO) > 0){

                            selectedGifts.add(giftList[item]);
                            StringBuilder text = new StringBuilder();
                            for (int i = 0; i < selectedGifts.size(); i++) {
                                text.append(selectedGifts.get(i)).append("\n");
                            }
                            selectedGiftTextView.setText(text.toString());

                            creditTotal.subtract(new BigDecimal((item + 1) * 10));
                            creditTotal = creditTotal.subtract(new BigDecimal((item + 1) * 10));
                            creditLeftTx.setText("you have credit left: " + creditTotal);


                        }
                    }
                });

                //Creating CANCEL button in alert dialog, to dismiss the dialog box when nothing is selected
                builder.setCancelable(false)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //When clicked on CANCEL button the dalog will be dismissed
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        initViews();

        if (savedInstanceState != null) {
            if (imageUri != null) {
                imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
                txtResultBody.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
            }
        }

        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();

        if (!detector.isOperational()) {
            txtResultBody.setText("Detector initialisation failed");
            return;
        }


    }

    private void initViews() {
        txtResultBody = findViewById(R.id.txtResultsBody);
        btnOpenCamera = findViewById(R.id.btnOpenCamera);
        txtResultBody = findViewById(R.id.txtResultsBody);
//        btnOpenCamera.setOnClickListener(this);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creditTotal = creditTotalOri;
                creditLeftTx.setText("you have credit left: " + creditTotal);
                selectedGiftTextView.setText("");


            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnOpenCamera:
                ActivityCompat.requestPermissions(RewardActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takeBarcodePicture();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {


                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    for (int index = 0; index < barcodes.size(); index++) {
                        Barcode code = barcodes.valueAt(index);
                        txtResultBody.setText(txtResultBody.getText() + "\n" + code.displayValue + "\n");

                        int type = barcodes.valueAt(index).valueFormat;
                        switch (type) {
                            case Barcode.CONTACT_INFO:
                                Log.i(TAG, code.contactInfo.title);
                                break;
                            case Barcode.EMAIL:
                                Log.i(TAG, code.displayValue);
                                break;
                            case Barcode.ISBN:
                                Log.i(TAG, code.rawValue);
                                break;
                            case Barcode.PHONE:
                                Log.i(TAG, code.phone.number);
                                break;
                            case Barcode.PRODUCT:
                                Log.i(TAG, code.rawValue);
                                break;
                            case Barcode.SMS:
                                Log.i(TAG, code.sms.message);
                                break;
                            case Barcode.TEXT:
                                Log.i(TAG, code.displayValue);
                                break;
                            case Barcode.URL:
                                Log.i(TAG, "url: " + code.displayValue);
                                break;
                            case Barcode.WIFI:
                                Log.i(TAG, code.wifi.ssid);
                                break;
                            case Barcode.GEO:
                                Log.i(TAG, code.geoPoint.lat + ":" + code.geoPoint.lng);
                                break;
                            case Barcode.CALENDAR_EVENT:
                                Log.i(TAG, code.calendarEvent.description);
                                break;
                            case Barcode.DRIVER_LICENSE:
                                Log.i(TAG, code.driverLicense.licenseNumber);
                                break;
                            default:
                                Log.i(TAG, code.rawValue);
                                break;
                        }
                    }
                    if (barcodes.size() == 0) {
                        txtResultBody.setText("No barcode could be detected. Please try again.");
                    }
                } else {
                    txtResultBody.setText("Detector initialisation failed");
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, e.toString());
            }
        }
    }

    private void takeBarcodePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "pic.jpg");
        imageUri = FileProvider.getUriForFile(RewardActivity.this,
                BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, txtResultBody.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
