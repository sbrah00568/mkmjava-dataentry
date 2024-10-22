package com.momworks.dataentry.constant;

public class SehatIndoConstants {

    public static final String BAYI = "bayi";
    public static final String BADUTA = "baduta";
    public static final String SHEET_NAME = "Sheet1";
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int NINE = 9;
    public static final int TWELVE = 12;
    public static final int EIGHTEEN = 18;
    public static final int TWENTY_FOUR = 24;
    public static final String POINT_ZERO = ".0";
    public static final String SPACE = " ";
    public static final String EMPTY = "";
    public static final String HYPHEN = "-";
    public static final String DALAM_GEDUNG = "DALAM GEDUNG";
    public static final String ATTR_KEY_CONTENT_DESC = "content-desc";
    public static final String PYD = "PYD";
    public static final String POSYANDU = "POSYANDU";
    public static final String HYPHEN_WANASARI = "-WANASARI";
    public static final String PILIH = "Pilih";
    public static final String HASIL_PENCARIAN = "Hasil pencarian";
    public static final String WANASARI = "WANASARI";
    public static final String LANGUAGE = "in";
    public static final String COUNTRY = "ID";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd";

    // XPATH Format = XPATH + PAGE + (button name, edit text name, menu name)
    public static final String XPATH_LOGIN_NOMOR_TELEPON_FIELD = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View//android.widget.EditText";
    public static final String XPATH_LOGIN_NOMOR_TELEPON_DISMISS = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View";
    public static final String XPATH_LOGIN_LANJUT_BUTTON = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View//android.view.View[@content-desc=\"Lanjut\"]";
    public static final String XPATH_INPUT_PIN_FIELD = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View[2]//android.widget.EditText";
    public static final String XPATH_INPUT_PIN_SIMPAN_BUTTON = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View//android.view.View[@content-desc=\"Simpan\"]";
    public static final String XPATH_BERANDA_IMUNISASI_MENU = "//android.widget.ScrollView//android.view.View[@content-desc=\"Imunisasi\"]";
    public static final String XPATH_IMUNISASI_IMUNISASI_RUTIN_MENU = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View//android.widget.ImageView[@content-desc=\"Imunisasi Rutin\"]";
    public static final String XPATH_IMUNISASI_RUTIN_TANGGAL_SEEKBAR = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View[3]/android.widget.ImageView";
    public static final String XPATH_IMUNISASI_RUTIN_POS_IMUNISASI_DROPDOWN = "//android.widget.EditText";
    public static final String XPATH_POS_IMUNISASI_SEARCH_LOGO = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.widget.ImageView[2]";
    public static final String XPATH_POS_IMUNISASI_CARI_DISINI_FIELD = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.widget.ImageView[2]";
    public static final String XPATH_POS_IMUNISASI_CARI_BUTTON = "//android.view.View[@content-desc=\"Cari pos imunisasi\"]";
    public static final String XPATH_HASIL_PENCARIAN_PAGE = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.view.View";
    public static final String XPATH_HASIL_PENCARIAN_TIDAK_DITEMUKAN_BACK_BUTTON = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.widget.ImageView[1]";
    public static final String XPATH_HASIL_PENCARIAN_ELEMENT = "//android.view.View[@content-desc=\"%s\"]";
    public static final String XPATH_HASIL_PENCARIAN_PILIH_BUTTON = "//android.view.View[@content-desc=\"Pilih\"]";
    public static final String XPATH_IMUNISASI_RUTIN_BACK_BUTTON = "//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.View/android.view.View/android.view.View/android.view.View[1]/android.widget.ImageView";

    private SehatIndoConstants() {
        // This private constructor prevent instantiation of the class
        // and effectively turns the class into a utility class.
    }

}
