package br.com.rooting.liquid.core;

import br.com.rooting.liquid.Person;
import br.com.rooting.liquid.result.LiquidObject;
import br.com.rooting.liquid.result.LiquidProperty;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class SolidifierTest {

    @Test
    void test() {
        List<LiquidProperty> properties = new ArrayList<>();
        properties.add(new LiquidProperty("person.mother.phones[0].number", "+55 83 999468349"));
        properties.add(new LiquidProperty("person.phones[0].type", "MOBILE"));
        properties.add(new LiquidProperty("person.address.street", "Avenida 11 de Outubro"));
        properties.add(new LiquidProperty("person.mother.address.city", "João Pessoa"));
        properties.add(new LiquidProperty("person.address.number", "766"));
        properties.add(new LiquidProperty("person.mother.bithDate", "1958-12-13"));
        properties.add(new LiquidProperty("person.father.name", "Luís Antõnio Fialho da Costa"));
        properties.add(new LiquidProperty("person.father.bithDate", "1962-12-10"));
        properties.add(new LiquidProperty("person.father.address.distric", "Costa e Silva"));
        properties.add(new LiquidProperty("person.mother.phones[0].type", "MOBILE"));
        properties.add(new LiquidProperty("person.mother.address.country", "Brazil"));
        properties.add(new LiquidProperty("person.document", "08043042039"));
        properties.add(new LiquidProperty("person.bithDate", "1992-12-11"));
        properties.add(new LiquidProperty("person.father.phones[0].number", "+55 83 999027648"));
        properties.add(new LiquidProperty("person.father.document", "49258128079"));
        properties.add(new LiquidProperty("person.mother.address.number", "177"));
        properties.add(new LiquidProperty("person.address.city", "São Paulo"));
        properties.add(new LiquidProperty("person.phones[1].type", "LANDLINE"));
        properties.add(new LiquidProperty("person.address.distric", "Braz"));
        properties.add(new LiquidProperty("person.father.address.street", "Rua Eduardo Hugo Luis do Nascimento"));
        properties.add(new LiquidProperty("person.address.state", "São Paulo"));
        properties.add(new LiquidProperty("person.father.phones[0].type", "MOBILE"));
        properties.add(new LiquidProperty("person.phones[0].number", "+55 83 999031654"));
        properties.add(new LiquidProperty("person.father.address.number", "177"));
        properties.add(new LiquidProperty("person.father.gender", "MALE"));
        properties.add(new LiquidProperty("person.name", "Bruno Costa"));
        properties.add(new LiquidProperty("person.gender", "MALE"));
        properties.add(new LiquidProperty("person.mother.gender", "FAMALE"));
        properties.add(new LiquidProperty("person.father.address.state", "Paraíba"));
        properties.add(new LiquidProperty("person.father.address.city", "João Pessoa"));
        properties.add(new LiquidProperty("person.mother.document", "99522720062"));
        properties.add(new LiquidProperty("person.mother.address.street", "Rua Eduardo Hugo Luis do Nascimento"));
        properties.add(new LiquidProperty("person.father.address.country", "Brazil"));
        properties.add(new LiquidProperty("person.mother.address.distric", "Costa e Silva"));
        properties.add(new LiquidProperty("person.mother.name", "Maria de Fatima Dias"));
        properties.add(new LiquidProperty("person.mother.address.state", "Paraíba"));
        properties.add(new LiquidProperty("person.email", "brunoluisncosta@gmail.com"));
        properties.add(new LiquidProperty("person.phones[1].number", "+55 83 32334329"));
        properties.add(new LiquidProperty("person.address.country", "Brazil"));
        LiquidObject liquidObject = LiquidObject.create(properties);
        Solidifier<Person> solidifier = new Solidifier<>(new Person(), liquidObject);
        Person person = solidifier.solidify();
        System.out.println(person);
    }
}
