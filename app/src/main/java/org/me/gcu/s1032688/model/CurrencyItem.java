package org.me.gcu.s1032688.model;

import androidx.annotation.Nullable;
public class CurrencyItem {public String code;        // e.g. USD
    public String displayName; // e.g. United States Dollar
    public double rate;        // 1 GBP = rate CODE
    @Nullable public String link;
    @Nullable public String pubDate;
}
