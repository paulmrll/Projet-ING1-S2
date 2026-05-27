package org.example;

public class Client extends Person{
    private Field[] fields;
    public Client (String name, String firstname, String email, int age){
        super(age, name, firstname, email);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        return sb.toString();
    }

}
