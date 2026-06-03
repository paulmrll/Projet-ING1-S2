package org.example;

import java.util.Objects;

/**
 * Represents a person with personal details such as name, age, email,
 * and a unique system identifier.
 * * @author Paul MORILLE
 *
 * @version 1.0
 */
public class Person {
    /**
     * The last name of the person.
     */
    private String name;

    /**
     * The first name of the person.
     */
    private String firstname;

    /**
     * The unique identifier assigned to this person.
     */
    private final int id;

    /**
     * The age of the person in years.
     */
    private int age;

    /**
     * The email address of the person.
     */
    private String email;

    /**
     * The ground managed by this person.
     */
    private Ground ground;

    /**
     * Counter used to automatically generate unique identifiers for new instances.
     */
    private static int nbId = 0;

    /**
     * Constructs a new {@code Person} with the specified details.
     * Automatically increments and assigns a unique ID.
     *
     * @param age       the age of the person
     * @param name      the last name of the person
     * @param firstname the first name of the person
     * @param email     the email address of the person
     */
    public Person(int age, String name, String firstname, String email) {
        this.age = age;
        this.name = name;
        this.firstname = firstname;
        this.email = email;
        this.id = nbId;
        nbId++;
    }

    /**
     * Gets the email address of this person.
     *
     * @return the email address
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Gets the first name of this person.
     *
     * @return the first name
     */
    public String getFirstname() {
        return this.firstname;
    }

    /**
     * Gets the last name of this person.
     *
     * @return the last name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the unique identifier of this person.
     *
     * @return the unique ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the age of this person.
     *
     * @return the age in years
     */
    public int getAge() {
        return this.age;
    }

    /**
     * Sets the last name of this person.
     *
     * @param name the new last name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the first name of this person.
     *
     * @param firstname the new first name
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Sets the age of this person.
     *
     * @param age the new age
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Sets the email address of this person.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the ground managed by this person.
     *
     * @return the ground, or null if not assigned
     */
    public Ground getGround() {
        return ground;
    }

    /**
     * Sets the ground managed by this person.
     * Also updates the ground's owner reference.
     *
     * @param ground the ground to assign
     */
    public void setGround(Ground ground) {
        this.ground = ground;
        if (ground != null && ground.getOwner() != this) ground.setOwner(this);
    }

    /**
     * Compares this person to the specified object for equality.
     * Two persons are considered equal if and only if they share the exact same ID.
     *
     * @param O the object to compare with this person
     * @return {@code true} if the objects are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(Object O) {
        if (O instanceof Person p) {
            if (this.id == p.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a hash code value for this person.
     * The hash code is generated based exclusively on the person's unique ID.
     *
     * @return a hash code value for this person
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this person.
     * The string contains the id, name, firstname, age, and email,
     * each separated by a line break.
     *
     * @return a formatted string representing the person's details
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id);
        sb.append("\n");
        sb.append(this.name);
        sb.append("\n");
        sb.append(this.firstname);
        sb.append("\n");
        sb.append(this.age);
        sb.append("\n");
        sb.append(this.email);
        sb.append("\n");
        return sb.toString();
    }
}