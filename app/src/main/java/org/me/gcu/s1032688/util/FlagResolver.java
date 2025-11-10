package org.me.gcu.s1032688.util;

import android.content.Context;

import org.me.gcu.s1032688.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility that maps a 3-letter currency code to a drawable flag resource
 * using a simple CODE:cc mapping loaded from resources.
 */
public final class FlagResolver {

    private FlagResolver() {}

    private static Map<String, String> CUR_TO_CC = null;

    /** Lazily load the currency->country code mapping from resources. */
    private static synchronized void ensureLoaded(Context ctx) {
        if (CUR_TO_CC != null) return;
        Map<String, String> m = new HashMap<>();
        try {
            String[] arr = ctx.getResources().getStringArray(R.array.currency_flag_map);
            for (String item : arr) {
                if (item == null) continue;
                String s = item.trim();
                if (s.isEmpty()) continue;
                int p = s.indexOf(':');
                if (p <= 0) continue;
                String code = s.substring(0, p).trim().toUpperCase();
                String cc = s.substring(p + 1).trim().toLowerCase();
                if (code.length() == 3 && cc.length() == 2) m.put(code, cc);
            }
        } catch (Exception ignored) {
        }
        CUR_TO_CC = m;
    }

    /** Resolve a drawable resource id for the given currency code (or 0 if none). */
    public static int drawableFor(Context ctx, String currencyCode) {
        if (currencyCode == null) return 0;
        ensureLoaded(ctx);
        String cc = CUR_TO_CC.get(currencyCode.toUpperCase());
        if (cc == null) return 0;
        String resName = "flag_" + cc;
        int id = ctx.getResources().getIdentifier(resName, "drawable", ctx.getPackageName());
        return id;
    }
}
