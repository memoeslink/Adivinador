package com.app.memoeslink.adivinador.tagprocessor;

import android.util.Pair;

import org.memoeslink.IntegerHelper;
import org.memoeslink.StringHelper;

import java.util.HashMap;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class ActorRegistry {
    private static final HashMap<String, Supplier<Actor>> RESERVED_TAGS = new HashMap<>();
    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("^" + TagProcessor.ACTOR_ID_REGEX + "$");
    private static final Pattern SEQUENCE_SUFFIX = Pattern.compile("^.*_" + TagProcessor.INTEGER_REGEX + "$");
    private Actor defaultActor;
    private final HashMap<String, Pair<Actor, RecordTemporality>> compendium = new HashMap<>();
    private DuplicateHandling duplicateHandling;

    public ActorRegistry() {
        this(new Actor(), DuplicateHandling.DO_NOTHING, DefaultHandling.ANY);
    }

    public ActorRegistry(Actor defaultActor, DuplicateHandling duplicateHandling, DefaultHandling defaultHandling) {
        this.defaultActor = defaultActor != null ? defaultActor : new Actor();
        this.duplicateHandling = duplicateHandling != null ? duplicateHandling : DuplicateHandling.DO_NOTHING;

        if (RESERVED_TAGS.isEmpty())
            RESERVED_TAGS.put("default", this::getDefaultActor);
    }

    public Actor getDefaultActor() {
        return defaultActor;
    }

    public void setDefaultActor(Actor defaultActor) {
        this.defaultActor = defaultActor != null ? defaultActor : new Actor();
    }

    public DuplicateHandling getDuplicateHandling() {
        return duplicateHandling;
    }

    public void setDuplicateHandling(DuplicateHandling duplicateHandling) {
        this.duplicateHandling = duplicateHandling != null ? duplicateHandling : DuplicateHandling.DO_NOTHING;
    }

    public Actor get(String tag) {
        return compendium.get(tag).first;
    }

    public Actor getOrDefault(String tag) {
        return getOrDefault(tag, DefaultHandling.ANY);
    }

    public Actor getOrDefault(String tag, DefaultHandling defaultHandling) {
        defaultHandling = defaultHandling != null ? defaultHandling : DefaultHandling.ANY;

        if (RESERVED_TAGS.containsKey(tag))
            return RESERVED_TAGS.getOrDefault(tag, this::getDefaultActor).get();

        if ((defaultHandling == DefaultHandling.ANY) ||
                (defaultHandling == DefaultHandling.NULL && (StringHelper.isNullOrBlank(tag) || compendium.containsKey(tag))) ||
                (defaultHandling == DefaultHandling.NOT_FOUND && StringHelper.isNotNullOrBlank(tag) && !compendium.containsKey(tag))
        )
            return compendium.getOrDefault(tag, new Pair<>(defaultActor, RecordTemporality.PERMANENT)).first;
        return null;
    }

    public void put(String tag, Actor actor, RecordTemporality temporality) {
        if (actor == null || StringHelper.isNullOrBlank(tag)
                || !TAG_NAME_PATTERN.matcher(tag).matches())
            return;

        if (RESERVED_TAGS.containsKey(tag)) {
            if (tag.equals("default"))
                defaultActor = actor;
            return;
        }

        if (temporality == RecordTemporality.EPHEMERAL && !StringHelper.startsWith(tag, "@"))
            tag = "@" + tag;

        if (!compendium.containsKey(tag) || duplicateHandling == DuplicateHandling.REPLACE)
            compendium.put(tag, new Pair<>(actor, temporality));
        else if (duplicateHandling == DuplicateHandling.APPEND_SUFFIX) {
            int index = 2;
            String tempTag;

            if (SEQUENCE_SUFFIX.matcher(tag).matches()) {
                String sequence = StringHelper.substringAfterLast(tag, "_");
                tag = StringHelper.substringBeforeLast(tag, "_");
                index = IntegerHelper.tryParse(sequence, index);
            }

            do {
                tempTag = String.format("%s_%d", tag, ++index);
            } while (compendium.containsKey(tempTag) && index < Integer.MAX_VALUE);
            compendium.put(tempTag, new Pair<>(actor, temporality));
        }
    }

    public boolean has(String tag) {
        return compendium.containsKey(tag);
    }

    public void clear() {
        compendium.clear();
    }

    public void clearEphemeral() {
        compendium.values().removeIf(p -> p.second == RecordTemporality.EPHEMERAL);
    }

    public void clearTransitory() {
        compendium.values().removeIf(p -> p.second == RecordTemporality.TRANSITORY);
    }

    public void clearNonPermanent() {
        compendium.values().removeIf(p -> p.second != RecordTemporality.PERMANENT);
    }
}
