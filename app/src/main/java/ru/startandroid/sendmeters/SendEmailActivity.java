package ru.startandroid.sendmeters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.startandroid.sendmeters.ru.startandroid.sendmeters.datapicker.DataPicker;

public class SendEmailActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private EditText etKithenColdWaterMeter;
    private EditText etKithenHotWaterMeter;
    private EditText etBathroomColdWaterMeter;
    private EditText etBathroomHotWaterMeter;
    private EditText etElectricityMeter;
    private Button btnAddCounter;

    private List<ConstraintLayout> counters;
    private float fromPosition;
    private ViewFlipper flipper;
    private LayoutInflater inflater;

    String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddCounter = (Button) findViewById(R.id.btnAddCounter);
        btnAddCounter.setOnClickListener(this);
        counters = new ArrayList<>();
        // Устанавливаем listener касаний, для последующего перехвата жестов
        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
        mainLayout.setOnTouchListener(this);
        getSupportActionBar().hide();
        // Получаем объект ViewFlipper
        flipper = (ViewFlipper) findViewById(R.id.flipper);

        // Создаем View и добавляем их в уже готовый flipper
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //flipper.addView(createCounter());
        //flipper.addView(createCounter());
        //flipper.showNext();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddCounter:
                ConstraintLayout counter = createCounter();
                counters.add(counter);
                flipper.addView(counter);

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
        patternEmail = patternEmail.replace("{month}",month);
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
                    flipper.showNext();
                } else if (fromPosition < toPosition) {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_out));
                    flipper.showPrevious();
                    flipper.getCurrentView()
                }
            default:
                break;
        }
        return true;
    }


    private ConstraintLayout createCounter(){
        ConstraintLayout counter = (ConstraintLayout) inflater.inflate(R.layout.counter_template_layout, null);
        DataPicker dp1 = counter.findViewById(R.id.dp1);
        DataPicker dp10 = counter.findViewById(R.id.dp10);
        DataPicker dp100 = counter.findViewById(R.id.dp100);
        DataPicker dp1000 = counter.findViewById(R.id.dp1000);
        DataPicker dp10000 = counter.findViewById(R.id.dp10000);

        String[] months=new String[10];
        String[] months1=new String[10];
        String[] months2=new String[10];
        String[] months3=new String[10];
        String[] months4=new String[10];


        months[0]="0";
        months[1]="1";
        months[2]="2";
        months[3]="3";
        months[4]="4";
        months[5]="5";
        months[6]="6";
        months[7]="7";
        months[8]="8";
        months[9]="9";

        months1[0]="0";
        months1[1]="1";
        months1[2]="2";
        months1[3]="3";
        months1[4]="4";
        months1[5]="5";
        months1[6]="6";
        months1[7]="7";
        months1[8]="8";
        months1[9]="9";


        months2[0]="0";
        months2[1]="1";
        months2[2]="2";
        months2[3]="3";
        months2[4]="4";
        months2[5]="5";
        months2[6]="6";
        months2[7]="7";
        months2[8]="8";
        months2[9]="9";


        months3[0]="0";
        months3[1]="1";
        months3[2]="2";
        months3[3]="3";
        months3[4]="4";
        months3[5]="5";
        months3[6]="6";
        months3[7]="7";
        months3[8]="8";
        months3[9]="9";

        months4[0]="0";
        months4[1]="1";
        months4[2]="2";
        months4[3]="3";
        months4[4]="4";
        months4[5]="5";
        months4[6]="6";
        months4[7]="7";
        months4[8]="8";
        months4[9]="9";


        dp1.setValues(months);
        dp10.setValues(months1);
        dp100.setValues(months2);
        dp1000.setValues(months3);
        dp10000.setValues(months4);
        return counter;
    }
}
