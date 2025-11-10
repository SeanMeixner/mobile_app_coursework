package org.me.gcu.s1032688.ui;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

import org.me.gcu.s1032688.R;
import org.me.gcu.s1032688.util.FlagResolver;

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
        ImageView flag = findViewById(R.id.flag);
        Chip chip = findViewById(R.id.rateChip);
        androidx.appcompat.widget.Toolbar tb = findViewById(R.id.toolbar);
        if (tb != null) tb.setNavigationOnClickListener(v -> finish());

        title.setText(name + " (" + code + ")");
        rateTv.setText(String.format(Locale.UK, "1 GBP = %.4f %s", rate, code));
        gbpTo.setText("GBP \u2192 " + code);
        codeTo.setText(code + " \u2192 GBP");
        gbpTo.setChecked(true);

        if (flag != null) {
            int res = FlagResolver.drawableFor(this, code);
            if (res != 0) flag.setImageResource(res);
        }
        styleChipForRate(chip, rate);
        if (chip != null) {
            CharSequence label = chip.getText();
            chip.setContentDescription(getString(R.string.cd_rate_chip, label));
        }

        convert.setOnClickListener(v -> {
            String s = amount.getText().toString().trim();
            if (s.isEmpty()) { amount.setError("Enter an amount"); return; }
            try {
                double a = Double.parseDouble(s);
                if (a < 0) { amount.setError("Must be = 0"); return; }
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

    private void styleChipForRate(Chip chip, double r) {
        if (chip == null) return;
        int bg;
        String label;
        if (Double.isNaN(r)) {
            bg = 0xFF9E9E9E; label = "N/A";
        } else if (r < 1.0) {
            bg = getColor(R.color.rate_low); label = "Low";
        } else if (r < 5.0) {
            bg = getColor(R.color.rate_ok); label = "Stable";
        } else if (r < 10.0) {
            bg = getColor(R.color.rate_mid); label = "Elevated";
        } else {
            bg = getColor(R.color.rate_high); label = "High";
        }
        chip.setText(label);
        chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(bg));
        int rC = (bg >> 16) & 0xFF, gC = (bg >> 8) & 0xFF, bC = bg & 0xFF;
        double luminance = (0.299*rC + 0.587*gC + 0.114*bC) / 255.0;
        int textColor = luminance > 0.6 ? 0xFF000000 : 0xFFFFFFFF;
        chip.setTextColor(textColor);
    }
}
