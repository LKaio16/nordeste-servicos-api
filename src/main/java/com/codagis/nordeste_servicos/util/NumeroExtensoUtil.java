package com.codagis.nordeste_servicos.util;

public class NumeroExtensoUtil {

    private static final String[] UNIDADES = {
        "", "UM", "DOIS", "TRÊS", "QUATRO", "CINCO", "SEIS", "SETE", "OITO", "NOVE"
    };

    private static final String[] DEZENA_ESPECIAL = {
        "DEZ", "ONZE", "DOZE", "TREZE", "QUATORZE", "QUINZE", "DEZESSEIS", "DEZESSETE", "DEZOITO", "DEZENOVE"
    };

    private static final String[] DEZENAS = {
        "", "", "VINTE", "TRINTA", "QUARENTA", "CINQUENTA", "SESSENTA", "SETENTA", "OITENTA", "NOVENTA"
    };

    private static final String[] CENTENAS = {
        "", "CEM", "DUZENTOS", "TREZENTOS", "QUATROCENTOS", "QUINHENTOS", "SEISCENTOS", "SETECENTOS", "OITOCENTOS", "NOVECENTOS"
    };

    public static String converterParaExtenso(Double valor) {
        if (valor == null || valor == 0) {
            return "ZERO";
        }

        long parteInteira = valor.longValue();
        int centavos = (int) Math.round((valor - parteInteira) * 100);

        StringBuilder resultado = new StringBuilder();

        if (parteInteira > 0) {
            resultado.append(converterNumero(parteInteira));
            if (parteInteira == 1) {
                resultado.append(" REAL");
            } else {
                resultado.append(" REAIS");
            }
        }

        if (centavos > 0) {
            if (parteInteira > 0) {
                resultado.append(" E ");
            }
            resultado.append(converterNumero(centavos));
            if (centavos == 1) {
                resultado.append(" CENTAVO");
            } else {
                resultado.append(" CENTAVOS");
            }
        }

        return resultado.toString();
    }

    private static String converterNumero(long numero) {
        if (numero == 0) {
            return "";
        }

        if (numero < 10) {
            return UNIDADES[(int) numero];
        }

        if (numero < 20) {
            return DEZENA_ESPECIAL[(int) (numero - 10)];
        }

        if (numero < 100) {
            long dezena = numero / 10;
            long unidade = numero % 10;
            String resultado = DEZENAS[(int) dezena];
            if (unidade > 0) {
                resultado += " E " + UNIDADES[(int) unidade];
            }
            return resultado;
        }

        if (numero < 1000) {
            long centena = numero / 100;
            long resto = numero % 100;
            String resultado = CENTENAS[(int) centena];
            if (centena == 1 && resto > 0) {
                resultado = "CENTO";
            }
            if (resto > 0) {
                resultado += " E " + converterNumero(resto);
            }
            return resultado;
        }

        if (numero < 1000000) {
            long milhar = numero / 1000;
            long resto = numero % 1000;
            String resultado = converterNumero(milhar) + " MIL";
            if (resto > 0) {
                if (resto < 100) {
                    resultado += " E " + converterNumero(resto);
                } else {
                    resultado += " " + converterNumero(resto);
                }
            }
            return resultado;
        }

        if (numero < 1000000000) {
            long milhao = numero / 1000000;
            long resto = numero % 1000000;
            String resultado = converterNumero(milhao);
            if (milhao == 1) {
                resultado += " MILHÃO";
            } else {
                resultado += " MILHÕES";
            }
            if (resto > 0) {
                resultado += " " + converterNumero(resto);
            }
            return resultado;
        }

        return "VALOR MUITO GRANDE";
    }
}

