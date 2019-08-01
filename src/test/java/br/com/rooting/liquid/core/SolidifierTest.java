package br.com.rooting.liquid.core;

import br.com.rooting.liquid.Person;
import br.com.rooting.liquid.config.ConfigBuilder;
import br.com.rooting.liquid.core.solid.Solidifier;
import br.com.rooting.liquid.model.LiquidObject;
import br.com.rooting.liquid.model.LiquidProperty;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class SolidifierTest {

    @Test
    void test() {
        List<LiquidProperty> properties = new ArrayList<>();
        properties.add(new LiquidProperty("person.address.state", "São Paulo"));
        properties.add(new LiquidProperty("person.phones[1].countryCode", "+55"));
        properties.add(new LiquidProperty("person.phones[0].number", "999031654"));
        properties.add(new LiquidProperty("person.address.street", "Avenida 11 de Outubro"));
        properties.add(new LiquidProperty("person.address.number", "766"));
        properties.add(new LiquidProperty("person.address.district", "Braz"));
        properties.add(new LiquidProperty("bruno.cpf", "08043042039"));
        properties.add(new LiquidProperty("person.name", "Bruno Costa"));
        properties.add(new LiquidProperty("person.gender", "MALE"));
        properties.add(new LiquidProperty("person.phones[1].ddd", "83"));
        properties.add(new LiquidProperty("person.dataAniversario", "1992-12-11"));
        properties.add(new LiquidProperty("person.email", "brunoluisncosta@gmail.com"));
        properties.add(new LiquidProperty("person.address.city", "São Paulo"));
        properties.add(new LiquidProperty("person.phones[1].number", "32334329"));
        properties.add(new LiquidProperty("person.phones[0].ddd", "83"));
        properties.add(new LiquidProperty("person.phones[0].countryCode", "+55"));
        properties.add(new LiquidProperty("person.address.country", "Brazil"));
        LiquidObject liquidObject = LiquidObject.create(properties);
        ConfigBuilder configBuilder = new ConfigBuilder();
        Solidifier<Person> solidifier = new Solidifier<>(new Person(), liquidObject, configBuilder.build());
        Person person = solidifier.solidify();
        System.out.println(person);
    }
}
