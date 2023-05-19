package com.app.memoeslink.adivinador;

import com.memoeslink.common.Randomizer;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.StringHelper;

public class CoupleCompatibility {

    public static int calculate(String initialName, String finalName) {
        initialName = StringHelper.defaultIfBlank(initialName);
        finalName = StringHelper.defaultIfBlank(finalName);

        if (initialName.compareTo(finalName) < 0) {
            String tempName = initialName;
            initialName = finalName;
            finalName = tempName;
        }

        if (initialName.equalsIgnoreCase(finalName))
            return 100;
        else {
            long seed = LongHelper.getSeed(initialName + System.getProperty("line.separator") + finalName);
            return new Randomizer(seed).getInt(101);
        }
    }
}
