package org.example.models;


public class ClientForm {


    private String name;

    public String getName() {
        return this.name;
    }
    public void setName(String name) {

        this.name = name;
    }

    @Override
    public String toString() {
        return "ClientForm{name='" + name + "'}";
    }

}
