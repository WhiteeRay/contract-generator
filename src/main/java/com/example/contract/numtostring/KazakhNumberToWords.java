package com.example.contract.numtostring;

public class KazakhNumberToWords {
    private static final String[] units = {
            "", "бір", "екі", "үш", "төрт", "бес", "алты", "жеті", "сегіз", "тоғыз"
    };
    private static final String[] tens = {
            "", "он", "жиырма", "отыз", "қырық", "елу", "алпыс", "жетпіс", "сексен", "тоқсан"
    };

    public static String convert(long number) {
        if (number == 0) return "нөл";

        StringBuilder result = new StringBuilder();

        if (number >= 1_000_000) {
            result.append(convert(number / 1_000_000)).append(" миллион ");
            number %= 1_000_000;
        }
        if (number >= 1000) {
            if ((number / 1000) == 1) {
                result.append("мың ");
            } else {
                result.append(convert(number / 1000)).append(" мың ");
            }
            number %= 1000;
        }
        if (number >= 100) {
            result.append(units[(int)(number / 100)]).append(" жүз ");
            number %= 100;
        }
        if (number >= 10) {
            result.append(tens[(int)(number / 10)]).append(" ");
            number %= 10;
        }
        if (number > 0) {
            result.append(units[(int) number]).append(" ");
        }

        return result.toString().trim();
    }
}
