package com.app.memoeslink.adivinador;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    public int getInt(int start, int n) {
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

    public SimpleDate getDate() {
        long rangeStart = Timestamp.valueOf("1900-01-01 00:00:00").getTime();
        long rangeEnd = Timestamp.valueOf(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(new LocalDateTime())).getTime();
        long difference = rangeEnd - rangeStart + 1;
        DateTime dateTime = new DateTime(rangeStart + (long) (Math.random() * difference));
        return new SimpleDate(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
    }

    public SimpleTime getTime() {
        return new SimpleTime(getInt(0, 24), getInt(0, 60), getInt(0, 60));
    }
}
