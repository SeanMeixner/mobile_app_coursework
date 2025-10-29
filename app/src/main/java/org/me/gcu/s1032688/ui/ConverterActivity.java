package org.me.gcu.s1032688.ui;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import org.me.gcu.s1032688.R;

import java.util.Locale;

public class ConverterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_converter);

        String code = getIntent().getStringExtra("code");
        String name = getIntent().getStringExtra("name");
        double rate = getIntent().getDoubleExtra("rate", Double.NaN);

        TextView title = findViewById(R.id.title);
        TextView rateTv = findViewById(R.id.rate);
        RadioGroup dir = findViewById(R.id.dirGroup);
        RadioButton gbpTo = findViewById(R.id.gbpToCode);
        RadioButton codeTo = findViewById(R.id.codeToGbp);
        EditText amount = findViewById(R.id.amount);
        Button convert = findViewById(R.id.convert);
        TextView result = findViewById(R.id.result);

        title.setText(name + " (" + code + ")");
        rateTv.setText(String.format(Locale.UK, "1 GBP = %.4f %s", rate, code));
        gbpTo.setText("GBP → " + code);
        codeTo.setText(code + " → GBP");
        gbpTo.setChecked(true);

        convert.setOnClickListener(v -> {
            String s = amount.getText().toString().trim();
            if (s.isEmpty()) { amount.setError("Enter an amount"); return; }
            try {
                double a = Double.parseDouble(s);
                if (a < 0) { amount.setError("Must be ≥ 0"); return; }
                double out;
                if (gbpTo.isChecked()) {
                    out = a * rate;
                    result.setText(String.format(Locale.UK, "%.2f GBP = %.2f %s", a, out, code));
                } else {
                    out = a / rate;
                    result.setText(String.format(Locale.UK, "%.2f %s = %.2f GBP", a, code, out));
                }
            } catch (NumberFormatException e) {
                amount.setError("Invalid number");
            }
        });
    }
}
