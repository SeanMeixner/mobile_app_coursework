package org.me.gcu.s1032688.util;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Currency code -> 2-letter code -> flag_<code>.png in drawable-nodpi.
 * If there's no match or no drawable, returns 0 so UI can hide the ImageView.
 */
public final class FlagResolver {

    private FlagResolver() {}

    private static final Map<String, String> CUR_TO_CC = new HashMap<>();
    static {
        // majors
        CUR_TO_CC.put("GBP", "gb");
        CUR_TO_CC.put("USD", "us");
        CUR_TO_CC.put("EUR", "eu");
        CUR_TO_CC.put("JPY", "jp");

        // common extras (add/remove freely based on what flags you actually have)
        CUR_TO_CC.put("CHF", "ch");
        CUR_TO_CC.put("AUD", "au");
        CUR_TO_CC.put("CAD", "ca");
        CUR_TO_CC.put("NZD", "nz");
        CUR_TO_CC.put("CNY", "cn");
        CUR_TO_CC.put("HKD", "hk");
        CUR_TO_CC.put("SGD", "sg");
        CUR_TO_CC.put("SEK", "se");
        CUR_TO_CC.put("NOK", "no");
        CUR_TO_CC.put("DKK", "dk");
        CUR_TO_CC.put("ZAR", "za");
        CUR_TO_CC.put("INR", "in");
        CUR_TO_CC.put("BRL", "br");
        CUR_TO_CC.put("MXN", "mx");
        CUR_TO_CC.put("TRY", "tr");
        CUR_TO_CC.put("AED", "ae");
        CUR_TO_CC.put("PLN", "pl");
        CUR_TO_CC.put("CZK", "cz");
        CUR_TO_CC.put("HUF", "hu");
        CUR_TO_CC.put("RON", "ro");
        CUR_TO_CC.put("KRW", "kr");
        CUR_TO_CC.put("TWD", "tw");
        CUR_TO_CC.put("THB", "th");
        CUR_TO_CC.put("IDR", "id");
        CUR_TO_CC.put("MYR", "my");
        CUR_TO_CC.put("PHP", "ph");
        CUR_TO_CC.put("SAR", "sa");
        CUR_TO_CC.put("ILS", "il");
        CUR_TO_CC.put("EGP", "eg");
        CUR_TO_CC.put("NGN", "ng");
        CUR_TO_CC.put("KES", "ke");
        CUR_TO_CC.put("GHS", "gh");
        CUR_TO_CC.put("MAD", "ma");

        // Curaçao for ANG
        CUR_TO_CC.put("ANG", "cw");
    }

    public static int drawableFor(Context ctx, String currencyCode) {
        if (currencyCode == null) return 0;
        String cc = CUR_TO_CC.get(currencyCode.toUpperCase());
        if (cc == null) return 0;

        String resName = "flag_" + cc; // must match your PowerShell output
        int id = ctx.getResources().getIdentifier(resName, "drawable", ctx.getPackageName());
        return id;
    }
}
