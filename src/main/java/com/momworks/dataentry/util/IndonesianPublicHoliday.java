package com.momworks.dataentry.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IndonesianPublicHoliday {

    private IndonesianPublicHoliday() {
        // private constructor to hide the implicit public one.
    }

    private static final List<LocalDate> holidays = new ArrayList<>();

    // source: https://setkab.go.id/en/govt-announces-national-holidays-for-2024/
    static {
        // National Holidays
        holidays.add(LocalDate.of(2024, 1, 1));  // New Year 2024
        holidays.add(LocalDate.of(2024, 2, 8));  // Isra Mi’raj (Ascension Day) of Prophet Muhammad
        holidays.add(LocalDate.of(2024, 2, 10)); // Chinese New Year 2575 Kongzili
        holidays.add(LocalDate.of(2024, 3, 11)); // Holy Day of Silence, Saka New Year 1946
        holidays.add(LocalDate.of(2024, 3, 29)); // Good Friday
        holidays.add(LocalDate.of(2024, 3, 31)); // Easter Day
        holidays.add(LocalDate.of(2024, 4, 10)); // Eid Al-Fitr 1445 Hijri
        holidays.add(LocalDate.of(2024, 4, 11)); // Eid Al-Fitr 1445 Hijri
        holidays.add(LocalDate.of(2024, 5, 1));  // International Labor Day
        holidays.add(LocalDate.of(2024, 5, 9));  // Ascension of Jesus Christ
        holidays.add(LocalDate.of(2024, 5, 23)); // Vesak Day 2568 BE
        holidays.add(LocalDate.of(2024, 6, 1));  // Pancasila Day
        holidays.add(LocalDate.of(2024, 6, 17)); // Eid al-Adha 1445 Hijri
        holidays.add(LocalDate.of(2024, 7, 7));  // Islamic New Year 1446 Hijri
        holidays.add(LocalDate.of(2024, 8, 17)); // Independence Day of the Republic of Indonesia
        holidays.add(LocalDate.of(2024, 9, 16)); // Birthday of Prophet Muhammad
        holidays.add(LocalDate.of(2024, 12, 25)); // Christmas Day

        // Collective Leave Days
        holidays.add(LocalDate.of(2024, 2, 9));  // Collective Leave - Chinese New Year
        holidays.add(LocalDate.of(2024, 3, 12)); // Collective Leave - Holy Day of Silence
        holidays.add(LocalDate.of(2024, 4, 8));  // Collective Leave - Eid Al-Fitr
        holidays.add(LocalDate.of(2024, 4, 9));  // Collective Leave - Eid Al-Fitr
        holidays.add(LocalDate.of(2024, 4, 15)); // Collective Leave - Eid Al-Fitr
        holidays.add(LocalDate.of(2024, 5, 10)); // Collective Leave - Ascension of Jesus Christ
        holidays.add(LocalDate.of(2024, 5, 24)); // Collective Leave - Vesak Day
        holidays.add(LocalDate.of(2024, 6, 18)); // Collective Leave - Eid al-Adha
        holidays.add(LocalDate.of(2024, 12, 26)); // Collective Leave - Christmas Day
    }

    public static boolean isPublicHoliday(LocalDate date) {
        return holidays.contains(date);
    }
}
