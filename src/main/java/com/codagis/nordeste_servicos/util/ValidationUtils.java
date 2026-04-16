package com.codagis.nordeste_servicos.util;

public class ValidationUtils {

    private ValidationUtils() {}

    public static String onlyDigits(String value) {
        if (value == null) return null;
        return value.replaceAll("\\D", "");
    }

    public static String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String e = email.trim();
        if (e.isEmpty()) return false;
        return e.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidCpfCnpj(String cpfCnpj) {
        String doc = onlyDigits(cpfCnpj);
        if (doc == null) return false;
        if (doc.length() == 11) return isValidCPF(doc);
        if (doc.length() == 14) return isValidCNPJ(doc);
        return false;
    }

    private static boolean isValidCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) return false;
        if (cpf.chars().distinct().count() == 1) return false;

        int d1 = calcCPFDigit(cpf, 9);
        int d2 = calcCPFDigit(cpf, 10);
        return d1 == Character.getNumericValue(cpf.charAt(9)) &&
               d2 == Character.getNumericValue(cpf.charAt(10));
    }

    private static int calcCPFDigit(String cpf, int length) {
        int sum = 0;
        int weight = length + 1;
        for (int i = 0; i < length; i++) {
            int num = Character.getNumericValue(cpf.charAt(i));
            sum += num * (weight - i);
        }
        int mod = (sum * 10) % 11;
        return (mod == 10) ? 0 : mod;
    }

    private static boolean isValidCNPJ(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return false;
        if (cnpj.chars().distinct().count() == 1) return false;

        int d1 = calcCNPJDigit(cnpj, 12);
        int d2 = calcCNPJDigit(cnpj, 13);
        return d1 == Character.getNumericValue(cnpj.charAt(12)) &&
               d2 == Character.getNumericValue(cnpj.charAt(13));
    }

    private static int calcCNPJDigit(String cnpj, int length) {
        int[] weights = (length == 12)
                ? new int[]{5,4,3,2,9,8,7,6,5,4,3,2}
                : new int[]{6,5,4,3,2,9,8,7,6,5,4,3,2};
        int sum = 0;
        for (int i = 0; i < length; i++) {
            int num = Character.getNumericValue(cnpj.charAt(i));
            sum += num * weights[i];
        }
        int mod = sum % 11;
        return (mod < 2) ? 0 : (11 - mod);
    }
}
