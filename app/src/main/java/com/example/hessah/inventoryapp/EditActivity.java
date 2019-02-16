package com.example.hessah.inventoryapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

import com.example.hessah.inventoryapp.data.ProductContract.ProductEntry;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentPetUri;

    private EditText mNameEditText;
    private TextView mQuantityTextView;
    private EditText mPriceEditText;
    private EditText mSupplierEditText;
    private EditText mPhoneEditText;
    private ImageView mImageView;
    private Uri mImageURI;
    private ImageView mIncreaseQuantity;
    private ImageView mDecreaseQuantity;
    Integer quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();
        setTitle(getString(R.string.edit_product));
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        mNameEditText = findViewById(R.id.edit_text_name);
        mQuantityTextView = findViewById(R.id.text_view_quantity);
        mPriceEditText = findViewById(R.id.edit_text_price);
        mSupplierEditText = findViewById(R.id.edit_text_supplier);
        mPhoneEditText = findViewById(R.id.edit_text_phone);
        mImageView = findViewById(R.id.image);
        mIncreaseQuantity = findViewById(R.id.imageButtonIncrease);
        mDecreaseQuantity = findViewById(R.id.imageButtonDecrease);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissionREAD_EXTERNAL_STORAGE(EditActivity.this)) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            PermissionUtils.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                }
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductEntry.COLUMN_PRODUCT_Supplier,
                ProductEntry.COLUMN_PRODUCT_Phone};
        return new CursorLoader(this, mCurrentPetUri, projection, null, null, null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_Supplier);
            int phoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_Phone);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);
            String name = cursor.getString(nameColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            String imageURI = cursor.getString(imageColumnIndex);
            mNameEditText.setText(name);
            mQuantityTextView.setText(Integer.toString(quantity));
            mPriceEditText.setText(Float.toString(price));
            mSupplierEditText.setText(supplier);
            mPhoneEditText.setText(phone);
            if (imageURI != null) {
                mImageView.setImageURI(Uri.parse(imageURI));
            }
        }

    }

    public void EditQuantity(View view) {
        final Context context = this;
        mIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                mQuantityTextView.setText(Integer.toString(quantity));
            }
        });

        mDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( quantity >= 1 )
                    quantity--;
                else {
                    quantity = 0;
                    Toast.makeText(context, "Quantity is zero, can't be less.", Toast.LENGTH_SHORT).show();
                }
                mQuantityTextView.setText(Integer.toString(quantity));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityTextView.setText(Integer.toString(0));
        mPriceEditText.setText(Float.toString(0));
        mSupplierEditText.setText("");
        mPhoneEditText.setText("");
        mImageView.setImageDrawable(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(EditActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtils.MY_PERMISSIONS_READ_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            mImageURI = Uri.parse(selectedImage.toString());
            mImageView.setImageURI(selectedImage);
        }
    }


    public void onOrder(View view) {
        String phone = mPhoneEditText.getText().toString().trim();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Can't call. change the permission in your phone setting.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(callIntent);
    }


    public void onSave(View view) {
        saveProduct();
        finish();
    }

    private void saveProduct() {
        boolean update1, update2, update3, update4;
        int rowsAffected;
        ContentValues values = new ContentValues();
        String name = mNameEditText.getText().toString().trim();
        if (!name.equals("")) {
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
            update1 = true;
        }
        else {
            Toast.makeText(this, "Must enter name for product.", Toast.LENGTH_SHORT).show();
            update1 = false;
        }
        Integer quantity = Integer.parseInt(mQuantityTextView.getText().toString().trim());
        Float price = 0.0f;
        if (!"".equals(mPriceEditText.getText().toString().trim())) {
            price = Float.parseFloat(mPriceEditText.getText().toString().trim());
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
            update2 = true;
        }
        else{
            Toast.makeText(this, "Must enter price for product.", Toast.LENGTH_SHORT).show();
            update2 = false;
        }

        Bitmap icLanucher = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        Bitmap bitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
        if(!equals(icLanucher,bitmap) && mImageURI != null) {
            values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, mImageURI.toString());
        }
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        String supplier = mSupplierEditText.getText().toString().trim();
        if (!supplier.equals("")) {
            values.put(ProductEntry.COLUMN_PRODUCT_Supplier, supplier);
            update3 = true;
        }
        else {
            Toast.makeText(this, "Must enter supplier name.", Toast.LENGTH_SHORT).show();
            update3 = false;
        }

        String phone = mPhoneEditText.getText().toString().trim();
        if (!phone.equals("")) {
            values.put(ProductEntry.COLUMN_PRODUCT_Phone, phone);
            update4 = true;
        }
        else {
            Toast.makeText(this, "Must enter phone number.", Toast.LENGTH_SHORT).show();
            update4 = false;
        }

        if(update1 && update2 && update3 && update4) {
            rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_product_successful), Toast.LENGTH_SHORT).show();
            }
        }else
            Toast.makeText(this, getString(R.string.update_product_failed) + ". All fields must be filled.", Toast.LENGTH_SHORT).show();
    }


    public void onDelete(View view) {
        showDeleteConfirmationDialog();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_dialog_title));
        builder.setPositiveButton(getString(R.string.btn_delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.delete_product_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_product_successful), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public boolean equals(Bitmap bitmap1, Bitmap bitmap2) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    PermissionUtils.showPermissionDialog(context.getString(R.string.external_storage), context, Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionUtils.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            PermissionUtils.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                } else {
                    Toast.makeText(EditActivity.this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
