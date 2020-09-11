
package io.github.testeAssinc.async.rest.util;

import java.util.Random;
import javax.ejb.Stateless;


@Stateless
public class PrimeManager {

    public static final int DEFAULT_MAX_DECIMAL_PLACES = 8;
    public static final int DEFAULT_MIN_DECIMAL_PLACES = 4;

    public int generatePrimeNumber() {
        return generatePrimeNumber(DEFAULT_MIN_DECIMAL_PLACES, DEFAULT_MAX_DECIMAL_PLACES);
    }
    
    public int generatePrimeNumber(int minDecimalPlaces, int maxDecimalPlaces) {
        int decimalPlaces = getRandomBetween(minDecimalPlaces, maxDecimalPlaces);
        
        return generatePrimeNumber(decimalPlaces);
    }

    public int generatePrimeNumber(int decimalPlaces) {
        int min = 1, max = 10;
        for (int i = 0; i < decimalPlaces - 1; i++) {
            min = min * 10;
        }
        if (min > 1) {
            max = min * 2 - 1;
        }

        int primeNumber = 1;

        while (!isPrime(primeNumber)) {
            primeNumber = getRandomBetween(min, max);
        }

        return primeNumber;
    }

    private int getRandomBetween(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public boolean isPrime(int number) {
        if (number < 2) {
            return false;
        } else {
            for (int i = 2; i < number - 1; i++) {
                if ((number % i) == 0) {
                    return false;
                }
            }
            return true;
        }
    }

}
