package br.com.rooting.liquid.core;

import br.com.rooting.liquid.Address;
import br.com.rooting.liquid.Person;
import br.com.rooting.liquid.Phone;
import br.com.rooting.liquid.config.ConfigBuilder;
import br.com.rooting.liquid.result.LiquidObject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static br.com.rooting.liquid.Gender.FAMALE;
import static br.com.rooting.liquid.Gender.MALE;
import static br.com.rooting.liquid.LiquidObjectFormatter.formatAsJavaDeclaration;
import static br.com.rooting.liquid.Phone.Type.LANDLINE;
import static br.com.rooting.liquid.Phone.Type.MOBILE;

class LiquifierTest {

    @Test
    void liquifyPersonTest() {
        Person bruno = new Person();

        bruno.setName("Bruno Costa");
        bruno.setGender(MALE);
        bruno.setDocument("08043042039");
        bruno.setBirthDate(LocalDate.of(1992, 12, 11));

        Address brunoAddress = new Address("Avenida 11 de Outubro",
                                        "Braz",
                                        "766",
                                        "São Paulo",
                                        "São Paulo",
                                        "Brazil");
        bruno.setAddress(brunoAddress);

        Phone brunoPhone1 = new Phone(MOBILE, "+55 83 999031654");
        Phone brunoPhone2 = new Phone(LANDLINE, "+55 83 32334329");

        bruno.setEmail("brunoluisncosta@gmail.com");
        bruno.setPhones(Arrays.asList(brunoPhone1, brunoPhone2));

        Person father = new Person();
        father.setName("Luís Antõnio Fialho da Costa");
        father.setGender(MALE);
        father.setDocument("49258128079");
        father.setBirthDate(LocalDate.of(1962, 12, 10));

        Address fatherAddress = new Address("Rua Eduardo Hugo Luis do Nascimento",
                                            "Costa e Silva",
                                            "177",
                                            "João Pessoa",
                                            "Paraíba",
                                            "Brazil");

        father.setAddress(fatherAddress);

        Phone fatherPhone1 = new Phone(MOBILE, "+55 83 999027648");
        father.setPhones(Collections.singletonList(fatherPhone1));

        bruno.setFather(father);

        Person mother = new Person();
        mother.setName("Maria de Fatima Dias");
        mother.setGender(FAMALE);
        mother.setDocument("99522720062");
        mother.setBirthDate(LocalDate.of(1958, 12, 13));

        Address motherAddress = new Address("Rua Eduardo Hugo Luis do Nascimento",
                                            "Costa e Silva",
                                            "177",
                                            "João Pessoa",
                                            "Paraíba",
                                            "Brazil");

        mother.setAddress(motherAddress);

        Phone motherPhone1 = new Phone(MOBILE, "+55 83 999468349");
        mother.setPhones(Collections.singletonList(motherPhone1));

        bruno.setMother(mother);

        ConfigBuilder configBuilder = new ConfigBuilder();
        Liquifier liquifier = new Liquifier(bruno, configBuilder.build());
        LiquidObject liquid = liquifier.liquify();
        System.out.print(formatAsJavaDeclaration(liquid));
        liquid.asList().forEach(System.out::println);
    }

}
