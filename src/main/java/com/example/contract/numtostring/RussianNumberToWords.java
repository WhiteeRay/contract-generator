package com.example.contract.numtostring;

public class RussianNumberToWords {
    private static final String[] units = {
            "", "один", "два", "три", "четыре", "пять", "шесть",
            "семь", "восемь", "девять", "десять", "одиннадцать",
            "двенадцать", "тринадцать", "четырнадцать", "пятнадцать",
            "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"
    };

    private static final String[] tens = {
            "", "", "двадцать", "тридцать", "сорок", "пятьдесят",
            "шестьдесят", "семьдесят", "восемьдесят", "девяносто"
    };

    private static final String[] hundreds = {
            "", "сто", "двести", "триста", "четыреста", "пятьсот",
            "шестьсот", "семьсот", "восемьсот", "девятьсот"
    };

    public static String convert(long number) {
        if (number == 0) return "ноль";

        StringBuilder result = new StringBuilder();

        if (number >= 1_000_000) {
            result.append(convert(number / 1_000_000)).append(" миллион ");
            number %= 1_000_000;
        }

        if (number >= 1000) {
            long thousands = number / 1000;
            if (thousands == 1) {
                result.append("тысяча ");
            } else if (thousands == 2) {
                result.append("две тысячи ");
            } else {
                result.append(convert(thousands)).append(" тысяч ");
            }
            number %= 1000;
        }

        if (number >= 100) {
            result.append(hundreds[(int)(number / 100)]).append(" ");
            number %= 100;
        }

        if (number >= 20) {
            result.append(tens[(int)(number / 10)]).append(" ");
            number %= 10;
        }

        if (number > 0) {
            result.append(units[(int) number]).append(" ");
        }

        return result.toString().trim();
    }
}

