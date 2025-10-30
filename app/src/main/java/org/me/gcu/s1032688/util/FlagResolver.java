package org.me.gcu.s1032688.util;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;


public final class FlagResolver {

    private FlagResolver() {}

    private static final Map<String, String> CUR_TO_CC = new HashMap<>();
    static {
        // Major currencies
        CUR_TO_CC.put("GBP", "gb"); // United Kingdom
        CUR_TO_CC.put("USD", "us"); // United States
        CUR_TO_CC.put("EUR", "eu"); // European Union (note: no eu flag, but keeping for reference)
        CUR_TO_CC.put("JPY", "jp"); // Japan

        // Europe
        CUR_TO_CC.put("CHF", "ch"); // Switzerland
        CUR_TO_CC.put("SEK", "se"); // Sweden
        CUR_TO_CC.put("NOK", "no"); // Norway
        CUR_TO_CC.put("DKK", "dk"); // Denmark
        CUR_TO_CC.put("PLN", "pl"); // Poland
        CUR_TO_CC.put("CZK", "cz"); // Czech Republic
        CUR_TO_CC.put("HUF", "hu"); // Hungary
        CUR_TO_CC.put("RON", "ro"); // Romania
        CUR_TO_CC.put("HRK", "hr"); // Croatia
        CUR_TO_CC.put("BGN", "bg"); // Bulgaria
        CUR_TO_CC.put("ISK", "is"); // Iceland
        CUR_TO_CC.put("MKD", "mk"); // North Macedonia
        CUR_TO_CC.put("ALL", "al"); // Albania
        CUR_TO_CC.put("MDL", "md"); // Moldova
        CUR_TO_CC.put("BAM", "ba"); // Bosnia and Herzegovina
        CUR_TO_CC.put("RSD", "rs"); // Serbia
        CUR_TO_CC.put("UAH", "ua"); // Ukraine
        CUR_TO_CC.put("BYN", "by"); // Belarus
        CUR_TO_CC.put("RUB", "ru"); // Russia
        CUR_TO_CC.put("GEL", "ge"); // Georgia
        CUR_TO_CC.put("AMD", "am"); // Armenia
        CUR_TO_CC.put("AZN", "az"); // Azerbaijan

        // Americas
        CUR_TO_CC.put("CAD", "ca"); // Canada
        CUR_TO_CC.put("MXN", "mx"); // Mexico
        CUR_TO_CC.put("BRL", "br"); // Brazil
        CUR_TO_CC.put("ARS", "ar"); // Argentina
        CUR_TO_CC.put("COP", "co"); // Colombia
        CUR_TO_CC.put("PEN", "pe"); // Peru
        CUR_TO_CC.put("CLP", "cl"); // Chile
        CUR_TO_CC.put("UYU", "uy"); // Uruguay
        CUR_TO_CC.put("PYG", "py"); // Paraguay
        CUR_TO_CC.put("BOB", "bo"); // Bolivia
        CUR_TO_CC.put("VEF", "ve"); // Venezuela (VES also)
        CUR_TO_CC.put("VES", "ve"); // Venezuela
        CUR_TO_CC.put("GYD", "gy"); // Guyana
        CUR_TO_CC.put("SRD", "sr"); // Suriname
        CUR_TO_CC.put("DOP", "do"); // Dominican Republic
        CUR_TO_CC.put("CRC", "cr"); // Costa Rica
        CUR_TO_CC.put("NIO", "ni"); // Nicaragua
        CUR_TO_CC.put("HNL", "hn"); // Honduras
        CUR_TO_CC.put("GTQ", "gt"); // Guatemala
        CUR_TO_CC.put("SVC", "sv"); // El Salvador
        CUR_TO_CC.put("PAB", "pa"); // Panama
        CUR_TO_CC.put("HTG", "ht"); // Haiti
        CUR_TO_CC.put("JMD", "jm"); // Jamaica
        CUR_TO_CC.put("BBD", "bb"); // Barbados
        CUR_TO_CC.put("BZD", "bz"); // Belize
        CUR_TO_CC.put("TTD", "tt"); // Trinidad and Tobago
        CUR_TO_CC.put("BMD", "bm"); // Bermuda
        CUR_TO_CC.put("KYD", "ky"); // Cayman Islands
        CUR_TO_CC.put("BSD", "bs"); // Bahamas
        CUR_TO_CC.put("XCD", "ag"); // East Caribbean Dollar (Antigua, Dominica, Grenada, St Lucia, St Vincent)
        CUR_TO_CC.put("ANG", "cw"); // Netherlands Antillean Guilder (Curaçao)
        CUR_TO_CC.put("AWG", "aw"); // Aruba

        // Asia-Pacific
        CUR_TO_CC.put("CNY", "cn"); // China
        CUR_TO_CC.put("HKD", "hk"); // Hong Kong
        CUR_TO_CC.put("INR", "in"); // India
        CUR_TO_CC.put("IDR", "id"); // Indonesia
        CUR_TO_CC.put("KRW", "kr"); // South Korea
        CUR_TO_CC.put("MYR", "my"); // Malaysia
        CUR_TO_CC.put("PHP", "ph"); // Philippines
        CUR_TO_CC.put("SGD", "sg"); // Singapore
        CUR_TO_CC.put("THB", "th"); // Thailand
        CUR_TO_CC.put("VND", "vn"); // Vietnam
        CUR_TO_CC.put("TWD", "tw"); // Taiwan
        CUR_TO_CC.put("PKR", "pk"); // Pakistan
        CUR_TO_CC.put("BDT", "bd"); // Bangladesh
        CUR_TO_CC.put("LKR", "lk"); // Sri Lanka
        CUR_TO_CC.put("NPR", "np"); // Nepal
        CUR_TO_CC.put("MMK", "mm"); // Myanmar
        CUR_TO_CC.put("KHR", "kh"); // Cambodia
        CUR_TO_CC.put("LAK", "la"); // Laos
        CUR_TO_CC.put("MNT", "mn"); // Mongolia
        CUR_TO_CC.put("KZT", "kz"); // Kazakhstan
        CUR_TO_CC.put("UZS", "uz"); // Uzbekistan
        CUR_TO_CC.put("KGS", "kg"); // Kyrgyzstan
        CUR_TO_CC.put("TJS", "tj"); // Tajikistan
        CUR_TO_CC.put("TMT", "tm"); // Turkmenistan
        CUR_TO_CC.put("AFN", "af"); // Afghanistan
        CUR_TO_CC.put("BTN", "bt"); // Bhutan
        CUR_TO_CC.put("MVR", "mv"); // Maldives

        // Oceania
        CUR_TO_CC.put("AUD", "au"); // Australia
        CUR_TO_CC.put("NZD", "nz"); // New Zealand
        CUR_TO_CC.put("FJD", "fj"); // Fiji
        CUR_TO_CC.put("PGK", "pg"); // Papua New Guinea
        CUR_TO_CC.put("SBD", "sb"); // Solomon Islands
        CUR_TO_CC.put("TOP", "to"); // Tonga
        CUR_TO_CC.put("WST", "ws"); // Samoa
        CUR_TO_CC.put("VUV", "vu"); // Vanuatu
        CUR_TO_CC.put("XPF", "pf"); // CFP franc (French Polynesia, New Caledonia, Wallis and Futuna)

        // Middle East
        CUR_TO_CC.put("AED", "ae"); // United Arab Emirates
        CUR_TO_CC.put("SAR", "sa"); // Saudi Arabia
        CUR_TO_CC.put("ILS", "il"); // Israel
        CUR_TO_CC.put("BHD", "bh"); // Bahrain
        CUR_TO_CC.put("OMR", "om"); // Oman
        CUR_TO_CC.put("JOD", "jo"); // Jordan
        CUR_TO_CC.put("LBP", "lb"); // Lebanon
        CUR_TO_CC.put("IQD", "iq"); // Iraq
        CUR_TO_CC.put("SYP", "sy"); // Syria
        CUR_TO_CC.put("YER", "ye"); // Yemen
        CUR_TO_CC.put("KWD", "kw"); // Kuwait
        CUR_TO_CC.put("QAR", "qa"); // Qatar
        CUR_TO_CC.put("IRR", "ir"); // Iran

        // Africa
        CUR_TO_CC.put("ZAR", "za"); // South Africa
        CUR_TO_CC.put("EGP", "eg"); // Egypt
        CUR_TO_CC.put("NGN", "ng"); // Nigeria
        CUR_TO_CC.put("KES", "ke"); // Kenya
        CUR_TO_CC.put("GHS", "gh"); // Ghana
        CUR_TO_CC.put("MAD", "ma"); // Morocco
        CUR_TO_CC.put("TND", "tn"); // Tunisia
        CUR_TO_CC.put("DZD", "dz"); // Algeria
        CUR_TO_CC.put("LYD", "ly"); // Libya
        CUR_TO_CC.put("SZL", "sz"); // Eswatini
        CUR_TO_CC.put("MUR", "mu"); // Mauritius
        CUR_TO_CC.put("MZN", "mz"); // Mozambique
        CUR_TO_CC.put("ZMW", "zm"); // Zambia
        CUR_TO_CC.put("BWP", "bw"); // Botswana
        CUR_TO_CC.put("NAD", "na"); // Namibia
        CUR_TO_CC.put("LSL", "ls"); // Lesotho
        CUR_TO_CC.put("ZWL", "zw"); // Zimbabwe
        CUR_TO_CC.put("AOA", "ao"); // Angola
        CUR_TO_CC.put("TZS", "tz"); // Tanzania
        CUR_TO_CC.put("UGX", "ug"); // Uganda
        CUR_TO_CC.put("RWF", "rw"); // Rwanda
        CUR_TO_CC.put("ETB", "et"); // Ethiopia
        CUR_TO_CC.put("SOS", "so"); // Somalia
        CUR_TO_CC.put("DJF", "dj"); // Djibouti
        CUR_TO_CC.put("ERN", "er"); // Eritrea
        CUR_TO_CC.put("SDG", "sd"); // Sudan
        CUR_TO_CC.put("SSP", "ss"); // South Sudan
        CUR_TO_CC.put("MWK", "mw"); // Malawi
        CUR_TO_CC.put("SCR", "sc"); // Seychelles
        CUR_TO_CC.put("KMF", "km"); // Comoros
        CUR_TO_CC.put("MGA", "mg"); // Madagascar
        CUR_TO_CC.put("XAF", "cm"); // Central African CFA franc (Cameroon, CAR, Chad, Congo, Gabon, Equatorial Guinea)
        CUR_TO_CC.put("XOF", "sn"); // West African CFA franc (Senegal, Benin, Burkina Faso, Côte d'Ivoire, Guinea-Bissau, Mali, Niger, Togo)
        CUR_TO_CC.put("GNF", "gn"); // Guinea
        CUR_TO_CC.put("LRD", "lr"); // Liberia
        CUR_TO_CC.put("SLL", "sl"); // Sierra Leone
        CUR_TO_CC.put("GMD", "gm"); // Gambia
        CUR_TO_CC.put("CVE", "cv"); // Cape Verde
        CUR_TO_CC.put("STN", "st"); // São Tomé and Príncipe
        CUR_TO_CC.put("CDF", "cd"); // Democratic Republic of the Congo
        CUR_TO_CC.put("BIF", "bi"); // Burundi

        // Other
        CUR_TO_CC.put("BND", "bn"); // Brunei
        CUR_TO_CC.put("MOP", "mo"); // Macau
        CUR_TO_CC.put("SHP", "sh"); // Saint Helena
        CUR_TO_CC.put("FKP", "fk"); // Falkland Islands
        CUR_TO_CC.put("GIP", "gi"); // Gibraltar
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
