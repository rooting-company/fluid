package br.com.rooting.liquid;

public class Phone {

    private Type type;

    private String number;

    public Phone() {}

    public Phone(Type type, String number) {
        this.type = type;
        this.number = number;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public enum Type {
        MOBILE,
        LANDLINE
    }


}
