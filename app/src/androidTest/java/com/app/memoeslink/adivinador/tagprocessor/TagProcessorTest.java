package com.app.memoeslink.adivinador.tagprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.GrammaticalNumber;
import com.memoeslink.generator.common.TextComponent;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TagProcessorTest {

    @Test
    public void replaceTags() {
        String s = "De hecho, fueron {actor:ellos; id:relatives; grammatical-indicator:plural-masculine} quienes visitaron a #{actress}. Ellos son muy {[considerad[o,a]]; plural-form:+s; influence:relatives}.";
        TagProcessor tagProcessor = getTestTagProcessor();
        TextComponent t = tagProcessor.replaceTags(s);
        assertEquals("De hecho, fueron ellos quienes visitaron a Diana. Ellos son muy considerados.", t.getText());
    }

    @Test
    public void replaceActorTags() throws Exception {
        String s = "{actor:Rodrigo; id:protagonist; grammatical-indicator:singular-masculine} is lost, but {actor:Sandra; id:deuteragonist; grammatical-indicator:singular-feminine} is looking for him.";
        TagProcessor tagProcessor = getTestTagProcessor();
        ProcessedText t = tagProcessor.replaceActorTags(s);
        assertEquals("Rodrigo is lost, but Sandra is looking for him.", t.getText());
        assertTrue(tagProcessor.getActorRegistry().has("protagonist"));
        assertEquals(Gender.MASCULINE, tagProcessor.getActorRegistry().get("protagonist").getGender());
        assertEquals(GrammaticalNumber.SINGULAR, tagProcessor.getActorRegistry().get("protagonist").getGrammaticalNumber());
        assertTrue(tagProcessor.getActorRegistry().has("deuteragonist"));
        assertEquals(Gender.FEMININE, tagProcessor.getActorRegistry().get("deuteragonist").getGender());
        assertEquals(GrammaticalNumber.SINGULAR, tagProcessor.getActorRegistry().get("deuteragonist").getGrammaticalNumber());
    }

    @Test
    public void replaceSimpleTags() throws Exception {
        String s = "#{}, #{secondaryActor}, #{actor}, #{extra}";
        TagProcessor tagProcessor = getTestTagProcessor();
        ProcessedText t = tagProcessor.replaceSimpleTags(s);
        assertEquals("Dummy, Jane Doe, John Doe, Dummy", t.getText());
    }

    @Test
    public void replaceResourceTags() throws Exception {
        String s = "{string:sound_name; index:0}";
        TagProcessor tagProcessor = getTestTagProcessor();
        ProcessedText t = tagProcessor.replaceResourceTags(s);
        assertEquals("blip1", t.getText());
    }

    @Test
    public void replaceWordTags() throws Exception {
        String s = "Ella siempre fue una mujer {[talentos[o,a]]; influence:actress}.";
        TagProcessor tagProcessor = getTestTagProcessor();
        ProcessedText t = tagProcessor.replaceWordTags(s);
        assertEquals("Ella siempre fue una mujer talentosa.", t.getText());
    }

    @Test
    public void replaceRandomTags() throws Exception {
        String s = "Animal elegido: {rand:{actor:perro; id:dog}; {actor:gato; id:cat}; liebre; {[[caballo,yegua]]; influence:animal}}.";
        TagProcessor tagProcessor = getTestTagProcessor();
        ProcessedText t = tagProcessor.replaceRandomTags(s);
        assertNotEquals(s, t.getText());
    }

    @Test
    public void replaceDateTimeTags() throws Exception {
        String s = "Hoy es {date-time:yyyy-MM-dd}. La hora es {date-time:HH:mm}.";
        TagProcessor tagProcessor = getTestTagProcessor();
        ProcessedText t = tagProcessor.replaceDateTimeTags(s);
        assertNotEquals(s, t.getText());
    }

    public TagProcessor getTestTagProcessor() {
        Context context = ApplicationProvider.getApplicationContext();
        return new TagProcessor.NewTagProcessorBuilder(context)
                .setDefaultActor(new Actor("Dummy", Gender.UNDEFINED, GrammaticalNumber.UNDEFINED))
                .setActor("actor", new Actor("John Doe", Gender.UNDEFINED, GrammaticalNumber.UNDEFINED))
                .setActor("secondaryActor", new Actor("Jane Doe", Gender.UNDEFINED, GrammaticalNumber.UNDEFINED))
                .setActor("actress", new Actor("Diana", Gender.FEMININE, GrammaticalNumber.SINGULAR))
                .setActor("animal", new Actor("animal", Gender.FEMININE, GrammaticalNumber.SINGULAR))
                .setData("hotel")
                .build();
    }
}
