package com.app.memoeslink.adivinador;

public class Person extends Entity {
    private String honoraryTitle;
    private String japaneseHonorific;
    private String postNominalLetters;
    private String nickname;
    private String username;
    private String address;
    private String email;
    private SimpleDate birthdate;

    public Person() {
        super();
        honoraryTitle = "";
        japaneseHonorific = "";
        postNominalLetters = "";
        nickname = "";
        username = "";
        address = "";
        email = "";
        birthdate = new SimpleDate();
    }

    public Person(Entity entity) {
        this();
        this.id = entity.id;
        this.sex = entity.sex;
        this.forename = entity.forename;
        this.surname = entity.surname;
        this.generationalTitle = entity.generationalTitle;
        this.name = entity.name;
    }

    public Person(int id, int sex, String forename, String surname, String generationalTitle, Name name, String honoraryTitle, String japaneseHonorific, String postNominalLetters, String nickname, String username, String address, String email, SimpleDate birthdate) {
        super(id, sex, forename, surname, generationalTitle, name);
        this.honoraryTitle = honoraryTitle;
        this.japaneseHonorific = japaneseHonorific;
        this.postNominalLetters = postNominalLetters;
        this.nickname = nickname;
        this.username = username;
        this.address = address;
        this.email = email;
        this.birthdate = birthdate;
    }

    public String getHonoraryTitle() {
        return honoraryTitle;
    }

    public void setHonoraryTitle(String honoraryTitle) {
        this.honoraryTitle = honoraryTitle;
    }

    public String getJapaneseHonorific() {
        return japaneseHonorific;
    }

    public void setJapaneseHonorific(String japaneseHonorific) {
        this.japaneseHonorific = japaneseHonorific;
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

    public SimpleDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(SimpleDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getFormattedBirthday() {
        return birthdate.getYear() + "/" + String.format("%02d", birthdate.getMonth()) + "/" + String.format("%02d", birthdate.getDay());
    }

    public String getFormattedUsername() {
        return Methods.formatText(username, "b,tt");
    }
}
