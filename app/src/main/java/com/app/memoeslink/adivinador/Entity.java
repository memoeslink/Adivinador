package com.app.memoeslink.adivinador;

/**
 * Created by Memoeslink on 02/09/2017.
 */

public class Entity {
    protected int id;
    protected int sex;
    protected String[] primitiveName;
    protected NameEnum nameType;

    public Entity() {
        id = -1;
        sex = -1;
        primitiveName = new String[]{""};
        nameType = NameEnum.EMPTY;
    }

    public Entity(int id, int sex, String[] primitiveName, NameEnum nameType) {
        this.id = id;
        this.sex = sex;
        this.primitiveName = primitiveName;
        this.nameType = nameType;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String[] getPrimitiveName() {
        return primitiveName;
    }

    public void setPrimitiveName(String[] primitiveName) {
        this.primitiveName = primitiveName;
    }

    public NameEnum getNameType() {
        return nameType;
    }

    public void setNameType(NameEnum nameType) {
        this.nameType = nameType;
    }
}
