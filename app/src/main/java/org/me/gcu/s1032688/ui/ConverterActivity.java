package org.me.gcu.s1032688.ui;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

import org.me.gcu.s1032688.R;
import org.me.gcu.s1032688.util.FlagResolver;

import java.util.Locale;

/**
 * Conversion screen for a selected currency against GBP.
 * Shows the current rate, a strength chip, and a two-way converter
 * using a modern segmented toggle for direction selection.
 */
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
        com.google.android.material.button.MaterialButtonToggleGroup dir = findViewById(R.id.dirToggle);
        com.google.android.material.button.MaterialButton btnGbpTo = findViewById(R.id.btnGbpToCode);
        com.google.android.material.button.MaterialButton btnCodeTo = findViewById(R.id.btnCodeToGbp);
        EditText amount = findViewById(R.id.amount);
        Button convert = findViewById(R.id.convert);
        TextView result = findViewById(R.id.result);
        ImageView flag = findViewById(R.id.flag);
        Chip chip = findViewById(R.id.rateChip);
        androidx.appcompat.widget.Toolbar tb = findViewById(R.id.toolbar);
        if (tb != null) tb.setNavigationOnClickListener(v -> finish());

        // Validate that we have a usable currency payload before wiring the UI
        if (code == null || name == null || Double.isNaN(rate) || rate <= 0.0) {
            Toast.makeText(this, getString(R.string.error_missing_rate), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Header content
        title.setText(name + " (" + code + ")");
        rateTv.setText(String.format(Locale.UK, "1 GBP = %.4f %s", rate, code));
        btnGbpTo.setText("GBP \u2192 " + code);
        btnCodeTo.setText(code + " \u2192 GBP");
        dir.check(R.id.btnGbpToCode);

        if (flag != null) {
            int res = FlagResolver.drawableFor(this, code);
            if (res != 0) flag.setImageResource(res);
        }
        styleChipForRate(chip, rate);
        if (chip != null) {
            CharSequence label = chip.getText();
            chip.setContentDescription(getString(R.string.cd_rate_chip, label));
        }

        // Convert on tap using the selected direction
        convert.setOnClickListener(v -> {
            String s = amount.getText().toString().trim();
            if (s.isEmpty()) { amount.setError(getString(R.string.error_enter_amount)); return; }
            try {
                double a = Double.parseDouble(s);
                if (a < 0) { amount.setError(getString(R.string.error_non_negative)); return; }
                double out;
                if (dir.getCheckedButtonId() == R.id.btnGbpToCode) {
                    out = a * rate;
                    result.setText(String.format(Locale.UK, "%.2f GBP = %.2f %s", a, out, code));
                } else {
                    out = a / rate;
                    result.setText(String.format(Locale.UK, "%.2f %s = %.2f GBP", a, code, out));
                }
            } catch (NumberFormatException e) {
                amount.setError(getString(R.string.error_invalid_number));
            }
        });
    }

    /**
     * Color-coded chip for highlighting rate magnitude.
     */
    private void styleChipForRate(Chip chip, double r) {
        if (chip == null) return;
        int bg;
        String label;
        if (Double.isNaN(r)) {
            bg = 0xFF9E9E9E; label = getString(R.string.rate_label_stable);
        } else if (r < 1.0) {
            bg = getColor(R.color.rate_low); label = getString(R.string.rate_label_low);
        } else if (r < 5.0) {
            bg = getColor(R.color.rate_ok); label = getString(R.string.rate_label_stable);
        } else if (r < 10.0) {
            bg = getColor(R.color.rate_mid); label = getString(R.string.rate_label_elevated);
        } else {
            bg = getColor(R.color.rate_high); label = getString(R.string.rate_label_high);
        }
        chip.setText(label);
        chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(bg));
        int rC = (bg >> 16) & 0xFF, gC = (bg >> 8) & 0xFF, bC = bg & 0xFF;
        double luminance = (0.299*rC + 0.587*gC + 0.114*bC) / 255.0;
        int textColor = luminance > 0.6 ? 0xFF000000 : 0xFFFFFFFF;
        chip.setTextColor(textColor);
    }
}
