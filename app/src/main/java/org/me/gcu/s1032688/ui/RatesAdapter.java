package org.me.gcu.s1032688.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.s1032688.R;
import org.me.gcu.s1032688.model.CurrencyItem;
import org.me.gcu.s1032688.util.FlagResolver;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * RecyclerView adapter showing a list of currency rates.
 * - Supports filtering by code, name and country aliases
 * - Shows a small flag and a colored chip indicating rate magnitude
 */
public class RatesAdapter extends RecyclerView.Adapter<RatesAdapter.Holder> {

    public interface OnRateClick { void onRateClicked(CurrencyItem item); }

    private final ArrayList<CurrencyItem> all = new ArrayList<>();
    private final ArrayList<CurrencyItem> shown = new ArrayList<>();
    private final OnRateClick clicker;
    private final Map<String, String[]> aliases = new HashMap<>();

    public RatesAdapter(ArrayList<CurrencyItem> seed, OnRateClick clicker, Context context) {
        this.clicker = clicker;
        loadAliases(context);
        submit(seed);
    }

    /** Replace current data with a new list. */
    public void submit(List<CurrencyItem> list) {
        all.clear(); shown.clear();
        if (list != null) { all.addAll(list); shown.addAll(list); }
        notifyDataSetChanged();
    }

    // Filter by code, currency name, or any configured country/region alias
    public void filter(String q) {
        shown.clear();
        if (q == null || q.trim().isEmpty()) {
            shown.addAll(all);
        } else {
            String s = norm(q);

            for (CurrencyItem c : all) {
                String name = c.displayName != null ? c.displayName : "";
                String code = c.code != null ? c.code : "";
                // match code or currency name
                if (norm(name).contains(s) || norm(code).contains(s)) {
                    shown.add(c);
                    continue;
                }
                // match country/region aliases for this currency code
                String[] aliases = this.aliases.get(code.toUpperCase(Locale.ROOT));
                if (aliases != null) {
                    boolean hit = false;
                    for (String a : aliases) {
                        if (norm(a).contains(s) || s.contains(norm(a))) { hit = true; break; }
                    }
                    if (hit) {
                        shown.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    /** Load per-currency country/region aliases from resources. */
    private void loadAliases(Context ctx) {
        try {
            String[] arr = ctx.getResources().getStringArray(R.array.currency_aliases);
            for (String item : arr) {
                if (item == null) continue;
                String trimmed = item.trim();
                if (trimmed.isEmpty()) continue;
                int sep = trimmed.indexOf(':');
                if (sep <= 0) continue;
                String code = trimmed.substring(0, sep).trim().toUpperCase(Locale.ROOT);
                String rest = trimmed.substring(sep + 1).trim();
                if (code.length() != 3 || rest.isEmpty()) continue;
                String[] vals = rest.split("\\|");
                ArrayList<String> cleaned = new ArrayList<>(vals.length);
                for (String v : vals) {
                    String t = v == null ? "" : v.trim();
                    if (!t.isEmpty()) cleaned.add(t);
                }
                if (!cleaned.isEmpty()) aliases.put(code, cleaned.toArray(new String[0]));
            }
        } catch (Exception ignored) {
            // leave aliases empty if resource missing or malformed
        }
    }

    // normalise: lowercase, strip accents/diacritics, remove punctuation/spaces
    private static String norm(String x) {
        String y = x == null ? "" : x.toLowerCase(Locale.ROOT).trim();
        y = Normalizer.normalize(y, Normalizer.Form.NFD).replaceAll("\\p{M}+", ""); // remove accents
        y = y.replaceAll("[^a-z0-9]", ""); // drop spaces/dashes/punct
        return y;
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_currency, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        CurrencyItem c = shown.get(position);

        h.title.setText(c.displayName + " (" + c.code + ")");
        h.sub.setText(String.format(Locale.UK, "1 GBP = %.4f %s", c.rate, c.code));

        // Emphasize rate with a colored chip instead of row background
        styleChipForRate(h, c.rate);

        int flagRes = FlagResolver.drawableFor(h.itemView.getContext(), c.code);
        if (flagRes != 0) {
            h.flag.setImageResource(flagRes);
            h.flag.setVisibility(View.VISIBLE);
        } else {
            h.flag.setImageResource(android.R.color.transparent);
            h.flag.setVisibility(View.INVISIBLE);
        }

        h.itemView.setOnClickListener(v -> clicker.onRateClicked(c));
    }

    @Override public int getItemCount() { return shown.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView flag; TextView title, sub; com.google.android.material.chip.Chip rateChip;
        Holder(@NonNull View v) {
            super(v);
            flag  = v.findViewById(R.id.flag);
            title = v.findViewById(R.id.title);
            sub   = v.findViewById(R.id.sub);
            rateChip = v.findViewById(R.id.rateChip);
        }
    }

    /** Simple 4+ bucket color scale for the rate magnitude. */
    private void styleChipForRate(Holder h, double r) {
        if (h.rateChip == null) return;
        int bg;
        String label;
        if (Double.isNaN(r)) {
            bg = 0xFF9E9E9E; label = h.itemView.getContext().getString(R.string.rate_label_stable);
        } else if (r < 1.0) {
            bg = h.itemView.getContext().getColor(R.color.rate_low); label = h.itemView.getContext().getString(R.string.rate_label_low);
        } else if (r < 5.0) {
            bg = h.itemView.getContext().getColor(R.color.rate_ok); label = h.itemView.getContext().getString(R.string.rate_label_stable);
        } else if (r < 10.0) {
            bg = h.itemView.getContext().getColor(R.color.rate_mid); label = h.itemView.getContext().getString(R.string.rate_label_elevated);
        } else {
            bg = h.itemView.getContext().getColor(R.color.rate_high); label = h.itemView.getContext().getString(R.string.rate_label_high);
        }
        h.rateChip.setText(label);
        h.rateChip.setChipBackgroundColor(ColorStateList.valueOf(bg));
        // Set readable text color depending on background luminance (simple heuristic)
        int rC = (bg >> 16) & 0xFF, gC = (bg >> 8) & 0xFF, bC = bg & 0xFF;
        double luminance = (0.299*rC + 0.587*gC + 0.114*bC) / 255.0;
        int textColor = luminance > 0.6 ? 0xFF000000 : 0xFFFFFFFF;
        h.rateChip.setTextColor(textColor);
    }
}
