package ru.startandroid.sendmeters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.startandroid.sendmeters.model.Counter;
import ru.startandroid.sendmeters.ru.startandroid.sendmeters.datapicker.DataPicker;

public class SendEmailActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private EditText etKithenColdWaterMeter;
    private EditText etKithenHotWaterMeter;
    private EditText etBathroomColdWaterMeter;
    private EditText etBathroomHotWaterMeter;
    private EditText etElectricityMeter;
    private Button btnAddCounter;

    private List<Counter> counters;
    private float fromPosition;
    private ViewFlipper flipper;
    private LayoutInflater inflater;

    String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counters = new ArrayList<>();
        // Устанавливаем listener касаний, для последующего перехвата жестов
        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
        mainLayout.setOnTouchListener(this);
        getSupportActionBar().hide();
        // Получаем объект ViewFlipper
        flipper = (ViewFlipper) findViewById(R.id.flipper);

        // Создаем View и добавляем их в уже готовый flipper
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ConstraintLayout addLayout = (ConstraintLayout) inflater.inflate(R.layout.add_layout, null);
        btnAddCounter = (Button) addLayout.findViewById(R.id.btnAddCounter);
        btnAddCounter.setOnClickListener(this);
        flipper.addView(addLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddCounter:

                createCounterDialog(flipper, counters);


               // counters.add(counterData);
               // flipper.addView(counterLayout);

        }
    }

    private void createEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //для того чтобы запросить email клиент устанавливаем тип
        emailIntent.setType("text/html");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.EmailTo)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getSubjectEmail());
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getTextEmail()));
        startActivity(Intent.createChooser(emailIntent, "Выберите email клиент :"));
    }

    private String getSubjectEmail() {
        Calendar calendar = Calendar.getInstance();
        String month = monthNames[calendar.get(Calendar.MONTH)].toLowerCase();
        String year = "" + calendar.get(Calendar.YEAR);
        String emailSubject = getResources().getString(R.string.EmailSubject);
        emailSubject = emailSubject.replace("{month}", month);
        emailSubject = emailSubject.replace("{year}", year);
        return emailSubject;
    }

    private String getTextEmail() {
        Calendar calendar = Calendar.getInstance();
        String month = monthNames[calendar.get(Calendar.MONTH)].toLowerCase();
        String kitchenColdWater = etKithenColdWaterMeter.getText().toString();
        String kitchenHotWater = etKithenHotWaterMeter.getText().toString();
        String bathroomColdWater = etBathroomColdWaterMeter.getText().toString();
        String bathroomHotWater = etBathroomHotWaterMeter.getText().toString();
        String electricity = etElectricityMeter.getText().toString();

        String patternEmail = getResources().getString(R.string.EmailText);
        patternEmail = patternEmail.replace("{month}", month);
        patternEmail = patternEmail.replace("{KithenColdWaterNumber}", getResources().getString(R.string.KithenColdWaterNumber));
        patternEmail = patternEmail.replace("{BathRoomColdWaterNumber}", getResources().getString(R.string.BathRoomColdWaterNumber));
        patternEmail = patternEmail.replace("{KithenHotWaterNumber}", getResources().getString(R.string.KithenHotWaterNumber));
        patternEmail = patternEmail.replace("{BathRoomHotWaterNumber}", getResources().getString(R.string.BathRoomHotWaterNumber));
        patternEmail = patternEmail.replace("{KithenColdWaterMeter}", kitchenColdWater);
        patternEmail = patternEmail.replace("{BathRoomColdWaterMeter}", bathroomColdWater);
        patternEmail = patternEmail.replace("{KithenHotWaterMeter}", kitchenHotWater);
        patternEmail = patternEmail.replace("{BathRoomHotWaterMeter}", bathroomHotWater);
        patternEmail = patternEmail.replace("{ElectricityMeter}", electricity);
        return patternEmail;


    }


    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: // Пользователь нажал на экран, т.е. начало движения
                // fromPosition - координата по оси X начала выполнения операции
                fromPosition = motionEvent.getX();
                break;
            case MotionEvent.ACTION_UP: // Пользователь отпустил экран, т.е. окончание движения
                float toPosition = motionEvent.getX();
                if (fromPosition > toPosition) {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.go_next_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.go_next_out));
                    if (counters.size() > 0) {
                        flipper.showNext();
                    }
                } else if (fromPosition < toPosition) {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_out));
                    if (counters.size() > 0) {
                        flipper.showPrevious();
                    }
                }
            default:
                break;
        }
        return true;
    }


    private ConstraintLayout createCounter(Counter counterData) {
        ConstraintLayout counter = (ConstraintLayout) inflater.inflate(R.layout.counter_template_layout, null);
        TextView tvRoom = counter.findViewById(R.id.tvNameRoom);
        TextView tvDescriptionCount = counter.findViewById(R.id.tvDescriptionCount);
        TextView tvNumberCount = counter.findViewById(R.id.tvNumberCount);

        tvRoom.setText(counterData.getRoom());
        tvDescriptionCount.setText(counterData.getDescription());
        tvNumberCount.setText(counterData.getNumber());

        DataPicker dp1 = counter.findViewById(R.id.dp1);
        DataPicker dp10 = counter.findViewById(R.id.dp10);
        DataPicker dp100 = counter.findViewById(R.id.dp100);
        DataPicker dp1000 = counter.findViewById(R.id.dp1000);
        DataPicker dp10000 = counter.findViewById(R.id.dp10000);
        String[] numbersCount;
        for (int i = 0; i < 5; i++) {
            numbersCount = new String[10];
            for (int j = 0; j < 10; j++) {
                numbersCount[j] = "" + j;
            }
            switch (i) {
                case 0:
                    dp1.setValues(numbersCount);
                    break;
                case 1:
                    dp10.setValues(numbersCount);
                    break;
                case 2:
                    dp100.setValues(numbersCount);
                    break;
                case 3:
                    dp1000.setValues(numbersCount);
                    break;
                case 4:
                    dp10000.setValues(numbersCount);
                    break;
            }

        }
        return counter;
    }


    private void createCounterDialog(final ViewFlipper flipper, final List<Counter> counters) {
        ConstraintLayout editCounter = (ConstraintLayout) inflater.inflate(R.layout.counter_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(editCounter);
        final EditText room = (EditText) editCounter.findViewById(R.id.etRoom);
        final EditText description = (EditText) editCounter.findViewById(R.id.etDescr);
        final EditText number = (EditText) editCounter.findViewById(R.id.etNumber);
        final Counter counter = new Counter();
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Добавить счетчик",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                counter.setDescription(description.getText().toString());
                                counter.setRoom(room.getText().toString());
                                counter.setNumber(number.getText().toString());
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ConstraintLayout counterLayout = createCounter(counter);
                        counter.setView(counterLayout);
                        counters.add(counter);
                        flipper.addView(counterLayout);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }
}
