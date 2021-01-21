package com.example.moneyid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = TrainingActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;
    @BindView(R.id.img_uji)
    ImageView imgUji;
    Button btntmbhUji;
    TextView btnTraining;
    EditText edtAddId;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        edtAddId = findViewById(R.id.EdtAddId);
        btntmbhUji = findViewById(R.id.btnTambahUji);
        btnTraining = findViewById(R.id.btnPnTra);
        btnTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tr = new
                        Intent(getApplicationContext(), TrainingActivity.class);
                startActivity(tr);
            }
        });
        btntmbhUji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doUpload(uri);
            }
        });
        loadUjiDefault();
        ImagePickerActivity.clearCache(this);
    }

    private void loadUji(String url) {
        Log.d(TAG, "Image cache path: " + url);
        GlideApp.with(this).load(url).into(imgUji);
        imgUji.setColorFilter(ContextCompat.getColor(this,
                android.R.color.transparent));
    }
    private void loadUjiDefault() {
        GlideApp.with(this).load(R.drawable.uang).into(imgUji);
        imgUji.setColorFilter(ContextCompat.getColor(this,R.color.colorAccent));
    }
//    private void loadUjiDefault() {
////        GlideApp.with(this).load(R.drawable.uang).into(imgUji);
//        imgUji.setColorFilter(ContextCompat.getColor(this, R.color.default_tint));
//    }

    @OnClick({R.id.img_uji})
    void onUjiImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void
                    onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }
                        if
                        (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void
                    onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
                    @Override
                    public void onTakeCameraSelected() {
                        launchCameraIntent();
                    }

                    @Override
                    public void onChooseGallerySelected() {
                        launchGalleryIntent();
                    }
                });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(MainActivity.this, ImagePickerActivity.class);

        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
                ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(MainActivity.this,
                ImagePickerActivity.class);

        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
                ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data.getParcelableExtra("path");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytearray = stream.toByteArray();

                    loadUji(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void doUpload(Uri uri) {
        ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        uri = uri.parse(uri.getPath());
        File file = new File(uri.getPath());
        RequestBody reqFile = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part mbody = MultipartBody.Part.createFormData("gambar", file.getName(),
                        reqFile);
        RequestBody id = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        (edtAddId.getText().toString().isEmpty() == true) ? "" :
                                edtAddId.getText().toString());
        RequestBody action = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        "post");
        Call<MResult> mResult = mApiInterface.postUji(mbody, id, action);
        mResult.enqueue(new Callback<MResult>() {
            @Override
            public void onResponse(Call<MResult> call, Response<MResult> response) {
                if (response.body().getStatus().equals("failed")) {
                    Toast.makeText(getApplicationContext(), "gagal", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onResponse() returned: " + response.body().getNominal());
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setMessage("Uang Nominal " + response.body().getNominal() +
                            "Merupakan Uang Rp. " + response.body().getRes());
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<MResult> call, Throwable t) {
                Log.e("log", t.toString());
                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Grant Permissions");
        builder.setMessage("his app needs permission to use this feature. You can grant them in app settings.");

        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });

        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
