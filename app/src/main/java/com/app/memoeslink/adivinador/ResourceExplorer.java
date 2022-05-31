package com.app.memoeslink.adivinador;

import android.content.Context;

import com.app.memoeslink.adivinador.finder.DatabaseFinder;
import com.app.memoeslink.adivinador.finder.PreferenceFinder;
import com.app.memoeslink.adivinador.finder.ReflectionFinder;
import com.memoeslink.generator.common.Explorer;
import com.memoeslink.generator.common.ExplorerReference;
import com.memoeslink.generator.common.IntegerHelper;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.finder.ResourceFinder;

public class ResourceExplorer extends Explorer {
    private final DatabaseFinder databaseFinder;
    private final PreferenceFinder preferenceFinder;
    private final ReflectionFinder reflectionFinder;

    public ResourceExplorer(Context context) {
        this(context, null);
    }

    public ResourceExplorer(Context context, Long seed) {
        super(context, seed);
        databaseFinder = new DatabaseFinder(context, seed);
        preferenceFinder = new PreferenceFinder(context, seed);
        reflectionFinder = new ReflectionFinder(context, seed);
    }

    public DatabaseFinder getDatabaseFinder() {
        return databaseFinder;
    }

    public PreferenceFinder getPreferenceFinder() {
        return preferenceFinder;
    }

    public ReflectionFinder getReflectionFinder() {
        return reflectionFinder;
    }

    @Override
    public void bindSeed(Long seed) {
        super.bindSeed(seed);
        databaseFinder.bindSeed(seed);
        preferenceFinder.bindSeed(seed);
        reflectionFinder.bindSeed(seed);
    }

    @Override
    public void unbindSeed() {
        super.unbindSeed();
        databaseFinder.unbindSeed();
        preferenceFinder.unbindSeed();
        reflectionFinder.unbindSeed();
    }

    public String findTableRowByName(String tableName) {
        return findTableRowByName(tableName, -1);
    }

    public String findTableRowByName(String tableName, int index) {
        if (index < 1)
            index = r.getInt(1, Database.getInstance(this).countTableRows(tableName));
        else
            index = IntegerHelper.defaultInt(index, 1, Database.getInstance(this).countTableRows(tableName));
        return Database.getInstance(this).selectFromTable(tableName, index);
    }

    public String findByReflection(String methodName) {
        String s = reflectionFinder.callMethod(methodName);

        if (s != null)
            return s;
        s = reflectionFinder.invokeMethod(methodName);

        if (s != null)
            return s;
        s = reflectionFinder.getMethodByName(methodName);

        if (StringHelper.isNotNullOrEmpty(s))
            return s;
        return "?";
    }

    public String getEmojis(int length) {
        length = IntegerHelper.defaultInt(length, 0, 1000);
        StringBuilder sb = new StringBuilder();

        for (int n = 0; n < length; n++) {
            sb.append(findByRef(ExplorerReference.EMOJI));
        }
        return sb.toString();
    }

    public String getPictogram() {
        switch (r.getInt(3)) {
            case 0:
                return "<b><font color=" + reflectionFinder.getDefaultColor() + ">" + findByRef(ExplorerReference.EMOTICON) + "</font></b>";
            case 1:
                return "<b><font color=" + reflectionFinder.getDefaultColor() + ">" + findByRef(ExplorerReference.KAOMOJI) + "</font></b>";
            case 2:
                return getEmojis(r.getInt(1, 4));
            default:
                return ResourceFinder.RESOURCE_NOT_FOUND;
        }
    }
}
