package org.me.gcu.s1032688.ui;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RatesAdapter extends RecyclerView.Adapter<RatesAdapter.Holder> {

    public interface OnRateClick { void onRateClicked(CurrencyItem item); }

    private final ArrayList<CurrencyItem> all = new ArrayList<>();
    private final ArrayList<CurrencyItem> shown = new ArrayList<>();
    private final OnRateClick clicker;

    public RatesAdapter(ArrayList<CurrencyItem> seed, OnRateClick clicker) {
        this.clicker = clicker;
        submit(seed);
    }

    public void submit(List<CurrencyItem> list) {
        all.clear(); shown.clear();
        if (list != null) { all.addAll(list); shown.addAll(list); }
        notifyDataSetChanged();
    }

    public void filter(String q) {
        shown.clear();
        if (q == null || q.trim().isEmpty()) {
            shown.addAll(all);
        } else {
            String s = q.toLowerCase(Locale.ROOT);
            for (CurrencyItem c : all) {
                if (c.displayName.toLowerCase(Locale.ROOT).contains(s) ||
                        c.code.toLowerCase(Locale.ROOT).contains(s)) {
                    shown.add(c);
                }
            }
        }
        notifyDataSetChanged();
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
        h.sub.setText(String.format(
                Locale.UK,
                "1 GBP = %.4f %s",
                c.rate,
                c.code
        ));

        // background tint by rate bucket
        int bg = colorForRate(c.rate);
        h.itemView.setBackgroundColor(bg);

        // show flag if we have it
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
        TextView title, sub;
        ImageView flag;
        Holder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.title);
            sub   = v.findViewById(R.id.sub);
            flag  = v.findViewById(R.id.flag);
        }
    }

    private int colorForRate(double r) {
        if (Double.isNaN(r)) return 0x00000000;
        if (r < 1.0)  return 0x332196F3; // light blue
        if (r < 5.0)  return 0x33A5D6A7; // light green
        if (r < 10.0) return 0x33FFD54F; // light amber
        return 0x33EF9A9A;               // light red
    }
}
