package br.com.rooting.liquid;

import br.com.rooting.liquid.converter.annotation.Converter;
import br.com.rooting.liquid.mapping.Alias;
import br.com.rooting.liquid.mapping.Ignore;

import java.time.LocalDate;
import java.util.List;

public class Person {

    private String name;

    private Gender gender;

    @Alias("dataAniversario")
    private LocalDate birthDate;

    @Alias(value = "bruno.cpf", root = true)
    private String document;

    private Address address;

    @Converter(liquify = PhoneConverter.class, solidify = PhoneConverter.class)
    private List<Phone> phones;

    private String email;

    @Ignore
    private Person father;

    @Ignore
    private Person mother;

    @Ignore
    private Person spouse;

    @Ignore
    private List<Person> offspring;

    @Ignore
    private RecursiveTest recursiveTest;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Person getFather() {
        return father;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public Person getMother() {
        return mother;
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }

    public Person getSpouse() {
        return spouse;
    }

    public void setSpouse(Person spouse) {
        this.spouse = spouse;
    }

    public List<Person> getOffspring() {
        return offspring;
    }

    public void setOffspring(List<Person> offspring) {
        this.offspring = offspring;
    }

    public RecursiveTest getRecursiveTest() {
        return recursiveTest;
    }

    public void setRecursiveTest(RecursiveTest recursiveTest) {
        this.recursiveTest = recursiveTest;
    }
}
