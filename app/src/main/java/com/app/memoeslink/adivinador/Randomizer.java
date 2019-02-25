package com.app.memoeslink.adivinador;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Memoeslink on 04/04/2018.
 */

public class Randomizer {
    private Random r;
    private Long seed;

    public Randomizer() {
        bindSeed(null);
    }

    public Randomizer(Long seed) {
        bindSeed(seed);
    }

    public void bindSeed(Long seed) {
        this.seed = seed;

        if (seed == null)
            this.r = new Random();
        else
            this.r = new Random(seed);
    }

    public Long getSeed() {
        return seed;
    }

    public int getInt(int n, int start) {
        int value;
        boolean negative = false;

        if (n < 1)
            n = 1;

        if (start < 0) {
            negative = true;
            start = Math.abs(start);
        }
        value = r.nextInt(n);

        if (negative)
            value = value - start;
        else
            value = value + start;
        return value;
    }

    public boolean getBoolean() {
        return r.nextBoolean();
    }

    public float getFloat() {
        return r.nextFloat();
    }

    public double getDouble() {
        return r.nextDouble();
    }

    public long getLong() {
        return r.nextLong();
    }

    public int getGaussianInt(int standardDeviation, int mean, int constraint) {
        int value;
        int tries = 999;

        try {
            do {
                value = (int) Math.round(r.nextGaussian() * standardDeviation + mean);
                tries--;
            } while (value <= constraint && tries > 0);

            if (tries == 0)
                value = constraint;
            return value;
        } catch (Exception e) {
            return constraint;
        }
    }

    public List<Integer> getIntegers(int maxNumbers, int maxValue, boolean nonRepeating) {
        List<Integer> numbers = new ArrayList<>();

        if (maxNumbers <= 0)
            maxNumbers = 1;

        if (maxValue <= 0)
            maxValue = 2;

        if (r != null) {
            for (int n = 0; n < maxNumbers; n++) {
                numbers.add(r.nextInt(maxValue + 1));
            }

            if (nonRepeating) {
                Set<Integer> set = new HashSet<>();
                set.addAll(numbers);
                numbers.clear();
                numbers.addAll(set);
            }
        }
        return numbers;
    }

    public Date getDate() {
        return new Date(Math.abs(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(getInt(36501, 0))));
    }
}
