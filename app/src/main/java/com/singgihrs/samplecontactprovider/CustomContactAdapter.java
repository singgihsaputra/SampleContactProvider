package com.singgihrs.samplecontactprovider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by singgihrs on 3/2/17.
 */

public class CustomContactAdapter extends SimpleCursorAdapter {

    Cursor cursor;

    Context mContext;

    LayoutInflater inflater;

    private int layout;

    public CustomContactAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
        int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
        this.layout=layout;
        cursor = c;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cr) {
        super.bindView(view, context, cursor);
        Holder holder;
        if (view == null) {
            holder = new Holder();
            holder.tvCOntactName = (TextView) view
                .findViewById(R.id.tvContactName);
            holder.tvContactNumber = (TextView) view
                .findViewById(R.id.tvContactNumber);
            holder.ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
            holder.tvCOntactName.setText(cursor.getString(cursor
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            holder.tvContactNumber.setText(cursor.getString(cursor
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            String imageUri = cursor.getString(cursor
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    mContext.getContentResolver(), Uri.parse(imageUri));
                holder.ivContactImage.setImageBitmap(bitmap);
                scaleImage(holder.ivContactImage);
            } catch (Exception e) {
                holder.ivContactImage.setImageResource(R.mipmap.ic_launcher);
                scaleImage(holder.ivContactImage);
            }

    }

    private void scaleImage(ImageView imageView) {
        Drawable drawing = imageView.getDrawable();
        if (drawing == null) {
        }
        Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = dpToPx(50);
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
            matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        imageView.setImageDrawable(result);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView
            .getLayoutParams();
        params.width = width;
        params.height = height;
        imageView.setLayoutParams(params);
    }

    private int dpToPx(int dp) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        cursor = c;
        return super.swapCursor(c);
    }

    class Holder {

        TextView tvCOntactName, tvContactNumber;

        ImageView ivContactImage;
    }
}