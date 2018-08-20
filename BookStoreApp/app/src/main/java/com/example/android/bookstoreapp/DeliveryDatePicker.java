package com.example.android.bookstoreapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

//credits - https://stackoverflow.com/questions/14933330/datepicker-how-to-popup-datepicker-when-click-on-edittext
public class DeliveryDatePicker implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    EditText dateEditText;
    private int day;
    private int month;
    private int year;
    private Context context;

    public DeliveryDatePicker(Context context, int editTextViewID) {
        Activity act = (Activity) context;
        this.dateEditText = (EditText) act.findViewById(editTextViewID);
        this.dateEditText.setOnClickListener(this);
        this.context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        month = monthOfYear;
        day = dayOfMonth;
        updateDisplay();
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(context, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();

    }

    // updates the date in the EditText
    private void updateDisplay() {

        dateEditText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(year).append("-").append(month + 1).append("-").append(day));
    }

}
