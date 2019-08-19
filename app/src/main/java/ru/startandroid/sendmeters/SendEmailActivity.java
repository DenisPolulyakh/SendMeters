package ru.startandroid.sendmeters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

public class SendEmailActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etKithenColdWaterMeter;
    private EditText etKithenHotWaterMeter;
    private EditText etBathroomColdWaterMeter;
    private EditText etBathroomHotWaterMeter;
    private EditText etElectricityMeter;
    private Button btnCreateEmail;
    String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    private String text = "<div>\n" +
            "Здравствуйте</div>\n" +
            "<div>\n" +
            "показания счетчиков за <strong>июль</strong>.</div>\n" +
            "<div>\n" +
            "Адрес: <strong>ул .4 - я линия, д .22 / 24</strong>.</div>\n" +
            "<div>\n" +
            "Квартира: <strong>101</strong>.</div>\n" +
            "<div>\n" +
            "Собственник: <strong>Полулях Д.М</strong>.</div>\n" +
            "<div>\n" +
            "ХВС 1(2710267) - 121,</div>\n" +
            "<div>\n" +
            "ХВС 2(28472685) - 271,</div>\n" +
            "<div>\n" +
            "ГВС 3(2634870) - 78,</div>\n" +
            "<div>\n" +
            "ГВС 4(28472811) - 148,</div>\n" +
            "<div>\n" +
            "Электросчетчик - 3823</div>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCreateEmail = (Button) findViewById(R.id.btnCreateEmail);
        btnCreateEmail.setOnClickListener(this);
        getSupportActionBar().hide();
        etKithenColdWaterMeter = (EditText) findViewById(R.id.etKithenColdWaterMeter);
        etKithenHotWaterMeter = (EditText) findViewById(R.id.etKithenHotWaterMeter);
        etBathroomColdWaterMeter = (EditText) findViewById(R.id.etBathroomColdWaterMeter);
        etBathroomHotWaterMeter = (EditText) findViewById(R.id.etBathroomHotWaterMeter);
        etElectricityMeter = (EditText) findViewById(R.id.etElectricityMeter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateEmail:
                createEmail();

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


}
