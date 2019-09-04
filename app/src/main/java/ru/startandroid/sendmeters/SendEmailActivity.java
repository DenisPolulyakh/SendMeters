package ru.startandroid.sendmeters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.startandroid.sendmeters.model.Counter;
import ru.startandroid.sendmeters.model.CounterList;
import ru.startandroid.sendmeters.ru.startandroid.sendmeters.datapicker.DataPicker;

public class SendEmailActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private Button btnAddCounter;
    private Button btnSendMeter;

    private List<Counter> counters;
    private float fromPosition;
    private ViewFlipper flipper;
    private LayoutInflater inflater;
    private SharedPreferences mPrefs;
    private final static String COUNTERS = "Counters";
    private final static String MONTH_SEND = "Month_send";
    private final static int DAY_START_SEND = 15;

    String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    String[] valiables = {"Кухня. Cчетчик холодной воды", "Кухня. Cчетчик горячей воды", "Ванная. Счетчик холодной воды", "Ванная. Cчетчик горячей воды", "Электрический счетчик"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        mPrefs = getPreferences(MODE_PRIVATE);
        counters = new ArrayList<>();
        // Устанавливаем listener касаний, для последующего перехвата жестов
        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.mainLayout);
        mainLayout.setOnTouchListener(this);

        // Получаем объект ViewFlipper
        flipper = (ViewFlipper) findViewById(R.id.flipper);

        // Создаем View и добавляем их в уже готовый flipper
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout addLayout = (ConstraintLayout) inflater.inflate(R.layout.add_layout, null);
        btnAddCounter = (Button) addLayout.findViewById(R.id.btnAddCounter);
        btnAddCounter.setOnClickListener(this);
        btnSendMeter = (Button) addLayout.findViewById(R.id.btnSendMeter);
        btnSendMeter.setOnClickListener(this);
        btnSendMeter.setVisibility(View.INVISIBLE);
        if (!loadFromPreference(MONTH_SEND).equalsIgnoreCase(getCurrentMonth())) {
            if (getCurrentDayOfMonth() >= DAY_START_SEND) {
                btnSendMeter.setVisibility(View.VISIBLE);
            }
        }
        flipper.addView(addLayout);


        CounterList counterList = loadCountersFromPreference();
        if (counterList != null) {
            int index = 0;
            for (Counter counter : counterList.getCountersList()) {
                ConstraintLayout counterLayout = createCounter(counter);
                TextView tc = ((TextView) counterLayout.findViewById(R.id.IndexLayout));
                tc.setText("" + index++);
                flipper.addView(counterLayout);
            }
            counters = counterList.getCountersList();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddCounter:
                createCounterDialog(flipper, counters);
                // counters.add(counterData);
                // flipper.addView(counterLayout);
                break;
            case R.id.btnDelete:
                deleteCounter(v);
                ViewParent vp = v.getParent();
                break;
            case R.id.btnSendMeter:
                createEmail();
                break;

        }
    }

    private void deleteCounter(View v) {
        View currentView = flipper.getCurrentView();
        String index = ((TextView) currentView.findViewById(R.id.IndexLayout)).getText().toString();
        counters.remove(Integer.parseInt(index));
        flipper.removeView(currentView);
        CounterList counterList = new CounterList(counters);
        saveCountersToPreference(counterList);

    }

    private void createEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //для того чтобы запросить email клиент устанавливаем тип
        emailIntent.setType("text/html");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.EmailTo)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getSubjectEmail());
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getTextEmail()));
        startActivity(Intent.createChooser(emailIntent, "Выберите email клиент :"));
        finish();
        saveToPreference(MONTH_SEND, getCurrentMonth());
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
        String patternEmail = getResources().getString(R.string.EmailText);
        patternEmail = patternEmail.replace("{month}", getCurrentMonth());
        String emailMeter;
        for (Counter c : counters) {
            emailMeter = "(" + c.getNumber() + ")" + " - " + Integer.parseInt(c.getMeter());
            if (c.getDescription().equalsIgnoreCase("Электрический счетчик")) {
                emailMeter = " - " + Integer.parseInt(c.getMeter());
            }
            patternEmail = patternEmail.replace(c.getDescription(), emailMeter);
        }
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
                        saveStateCounter(flipper);
                        flipper.showNext();

                    }
                } else if (fromPosition < toPosition) {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.go_prev_out));
                    if (counters.size() > 0) {
                        saveStateCounter(flipper);
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
        Button btnDelete = counter.findViewById(R.id.btnDelete);
        btnDelete.setVisibility(View.INVISIBLE);
        btnDelete.setOnClickListener(this);

        tvRoom.setText(counterData.getRoom());
        tvDescriptionCount.setText(counterData.getDescription());
        tvNumberCount.setText(counterData.getNumber());
        List<DataPicker> dataPickers = new ArrayList<>();
        dataPickers.add((DataPicker) counter.findViewById(R.id.dp10000));
        dataPickers.add((DataPicker) counter.findViewById(R.id.dp1000));
        dataPickers.add((DataPicker) counter.findViewById(R.id.dp100));
        dataPickers.add((DataPicker) counter.findViewById(R.id.dp10));
        dataPickers.add((DataPicker) counter.findViewById(R.id.dp1));
        String meter = counterData.getMeter();
        String[] numbersCount;
        for (DataPicker dp : dataPickers) {
            numbersCount = new String[10];
            for (int j = 0; j < 10; j++) {
                numbersCount[j] = "" + j;
            }
            dp.setValues(numbersCount);
        }
        if (meter != null) {
            for (int i = 4; i >= 0; i--) {
                dataPickers.get(i).changetToValue(Integer.parseInt("" + meter.charAt(i)));
            }
        }

        return counter;

    }


    private void createCounterDialog(final ViewFlipper flipper, final List<Counter> counters) {
        ConstraintLayout editCounter = (ConstraintLayout) inflater.inflate(R.layout.counter_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(editCounter);
        final EditText number = (EditText) editCounter.findViewById(R.id.etNumber);


        // Подключаем свой шаблон с разными значками
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valiables);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = (Spinner) editCounter.findViewById(R.id.SpiVar);
        spinner.setAdapter(adapter);
        final Counter counter = new Counter();
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Добавить счетчик",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                counter.setNumber(number.getText().toString());
                                counter.setDescription(spinner.getSelectedItem().toString());
                                if (spinner.getSelectedItem().toString().indexOf(".") != -1) {
                                    counter.setRoom(spinner.getSelectedItem().toString().substring(0, spinner.getSelectedItem().toString().indexOf(".")));
                                    counter.setDescription(spinner.getSelectedItem().toString().substring(spinner.getSelectedItem().toString().indexOf(".") + 1, spinner.getSelectedItem().toString().length()));
                                    counter.setDescription(spinner.getSelectedItem().toString());
                                } else {
                                    counter.setDescription(spinner.getSelectedItem().toString());
                                }
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
                        TextView tc = ((TextView) counterLayout.findViewById(R.id.IndexLayout));
                        tc.setText("" + counters.size());
                        counters.add(counter);
                        flipper.addView(counterLayout);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    @Override
    protected void onDestroy() {
        saveStateCounter(flipper);
        super.onDestroy();
    }

    private void saveStateCounter(ViewFlipper flipper) {
        ConstraintLayout counterView = (ConstraintLayout) flipper.getCurrentView();
        TextView indexTv = (TextView) counterView.findViewById(R.id.IndexLayout);
        if (indexTv != null) {
            Integer index = Integer.parseInt(indexTv.getText().toString());
            List<DataPicker> dataPickers = new ArrayList<>();
            dataPickers.add((DataPicker) counterView.findViewById(R.id.dp10000));
            dataPickers.add((DataPicker) counterView.findViewById(R.id.dp1000));
            dataPickers.add((DataPicker) counterView.findViewById(R.id.dp100));
            dataPickers.add((DataPicker) counterView.findViewById(R.id.dp10));
            dataPickers.add((DataPicker) counterView.findViewById(R.id.dp1));
            StringBuilder meter = new StringBuilder();
            for (DataPicker dp : dataPickers) {
                meter.append(dp.getValue());

            }
            Counter counter = counters.get(index);
            if (!meter.toString().equals(counter.getMeter())) {
                counter.setMeter(meter.toString());
                CounterList counterList = new CounterList(counters);
                saveCountersToPreference(counterList);
            }


        }
    }

    private void saveCountersToPreference(CounterList counterList) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(counterList);
        prefsEditor.putString(COUNTERS, json);
        prefsEditor.commit();

    }

    private CounterList loadCountersFromPreference() {
        Gson gson = new Gson();
        String json = mPrefs.getString(COUNTERS, "");
        CounterList counterList = gson.fromJson(json, CounterList.class);
        return counterList;
    }

    private void saveToPreference(String variable, String value) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(variable, value);
        prefsEditor.commit();

    }

    private String loadFromPreference(String variable) {
        String result = mPrefs.getString(variable, "");
        return result;
    }


    private String getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        String month = monthNames[calendar.get(Calendar.MONTH)].toLowerCase();
        return month;
    }

    private Integer getCurrentDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
}
