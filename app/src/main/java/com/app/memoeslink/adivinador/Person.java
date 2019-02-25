package com.app.memoeslink.adivinador;

import java.util.Date;

/**
 * Created by Memoeslink on 13/08/2017.
 */

public class Person extends Entity {
    private Entity entity;
    private String forename;
    private String lastName;
    private String titleOfHonor;
    private String japaneseHonorific;
    private String suffix;
    private String postNominalLetters;
    private String nickname;
    private String username;
    private String address;
    private String email;
    private Date birthdate;

    public Person() {
        entity = new Entity();
        forename = "";
        lastName = "";
        titleOfHonor = "";
        japaneseHonorific = "";
        suffix = "";
        postNominalLetters = "";
        nickname = "";
        username = "";
        address = "";
        email = "";
        birthdate = null;
    }

    public Person(Entity entity, String forename, String lastName, String titleOfHonor, String japaneseHonorific, String suffix, String postNominalLetters, String nickname, String username, String address, String email, Date birthdate) {
        this.entity = entity;
        this.forename = forename;
        this.lastName = lastName;
        this.titleOfHonor = titleOfHonor;
        this.japaneseHonorific = japaneseHonorific;
        this.suffix = suffix;
        this.postNominalLetters = postNominalLetters;
        this.nickname = nickname;
        this.username = username;
        this.address = address;
        this.email = email;
        this.birthdate = birthdate;
    }

    @Override
    public int getId() {
        return entity.getId();
    }

    @Override
    public void setId(int id) {
        this.entity.setId(id);
    }

    @Override
    public int getSex() {
        return entity.getSex();
    }

    @Override
    public void setSex(int sex) {
        this.entity.setSex(sex);
    }

    @Override
    public String[] getPrimitiveName() {
        return entity.getPrimitiveName();
    }

    @Override
    public void setNameType(NameEnum nameType) {
        this.entity.setNameType(nameType);
    }

    @Override
    public NameEnum getNameType() {
        return entity.getNameType();
    }

    @Override
    public void setPrimitiveName(String[] primitiveName) {
        this.entity.setPrimitiveName(primitiveName);
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitleOfHonor() {
        return titleOfHonor;
    }

    public void setTitleOfHonor(String titleOfHonor) {
        this.titleOfHonor = titleOfHonor;
    }

    public String getJapaneseHonorific() {
        return japaneseHonorific;
    }

    public void setJapaneseHonorific(String japaneseHonorific) {
        this.japaneseHonorific = japaneseHonorific;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPostNominalLetters() {
        return postNominalLetters;
    }

    public void setPostNominalLetters(String postNominalLetters) {
        this.postNominalLetters = postNominalLetters;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
}
