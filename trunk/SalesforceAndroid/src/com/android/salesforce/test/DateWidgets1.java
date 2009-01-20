/* 
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.salesforce.test;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker.OnTimeChangedListener;
import android.view.View;

import java.util.Calendar;

import com.android.R;

/**
 * Basic example of using date and time widgets, including
 * {@link android.app.TimePickerDialog} and {@link android.widget.DatePicker}.
 */
public class DateWidgets1 extends Activity {

    // where we display the selected date and time
    private TextView mDateDisplay;

    // date and time
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;


    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.date_widgets_example_1);

        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);

        Button pickDate = (Button) findViewById(R.id.pickDate);
        pickDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new DatePickerDialog(DateWidgets1.this,
                        mDateSetListener,
                        mYear, mMonth, mDay).show();
            }
        });

        Button pickTime = (Button) findViewById(R.id.pickTime);
        pickTime.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new TimePickerDialog(DateWidgets1.this,
                        mTimeSetListener, 
                        mHour, mMinute, false).show();
            }
        });

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        updateDisplay();
    }

    private void updateDisplay() {
        mDateDisplay.setText(
            new StringBuilder()
                    .append(mMonth).append("-")
                    .append(mDay).append("-")
                    .append(mYear).append(" ")
                    .append(pad(mHour)).append(":")
                    .append(pad(mMinute)));
    }

    private OnDateSetListener mDateSetListener =
            new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					
				}
            };

    private OnTimeSetListener mTimeSetListener =
            new OnTimeSetListener() {
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    updateDisplay();
                }

				@Override
				public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}                
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}
