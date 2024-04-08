package com.example.poly_truyen_client.utils;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DataConvertion {
    public String date(String inputDate) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = inputDateFormat.parse(inputDate);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void decorDialogBackground(Dialog dialog) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public String shortDesc(String content) {
        if (content.split("").length <= 150) {
            return content;
        } else {
            return content.substring(0, 145) + ".....";
        }
    }

    public void setRecyclerViewHeight(RecyclerView recyclerView) {
        int totalHeight = 0;
        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
            View listItem = recyclerView.getAdapter().onCreateViewHolder(recyclerView, recyclerView.getAdapter().getItemViewType(i)).itemView;
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = 150 + totalHeight + (recyclerView.getPaddingTop() + recyclerView.getPaddingBottom());
        recyclerView.setLayoutParams(params);
    }
}
