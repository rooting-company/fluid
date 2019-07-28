package br.com.rooting.liquid;

import br.com.rooting.liquid.converter.LiquifyConverter;
import br.com.rooting.liquid.converter.SolidifyConverter;
import br.com.rooting.liquid.result.LiquidObject;
import br.com.rooting.liquid.result.LiquidProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PhoneConverter implements LiquifyConverter<Phone>, SolidifyConverter<Phone> {

    @Override
    public List<LiquidProperty> convertToLiquid(Field field, String key, Phone phone) {
        List<LiquidProperty> properties = new ArrayList<>();

        String[] parts = phone.getNumber().split(" ");
        if (parts.length == 3) {
            LiquidProperty countryCode = new LiquidProperty(key + ".countryCode", parts[0]);
            LiquidProperty ddd = new LiquidProperty(key + ".ddd", parts[1]);
            LiquidProperty number = new LiquidProperty(key + ".number", parts[2]);

            properties.add(countryCode);
            properties.add(ddd);
            properties.add(number);
        }
        return properties;
    }

    @Override
    public Phone convertToSolid(Phone phone, String key, LiquidObject liquidObject) {
        LiquidProperty countryCode = liquidObject.findProperty(key + ".countryCode");
        LiquidProperty ddd = liquidObject.findProperty(key + ".ddd");
        LiquidProperty number = liquidObject.findProperty(key + ".number");

        if (countryCode != null && ddd != null && number != null) {
            if (phone == null) {
                phone = new Phone();
            }

            String fullNumber = countryCode.getValue()
                                            .concat(" ").concat(ddd.getValue())
                                            .concat(" ").concat(number.getValue());
            phone.setNumber(fullNumber);
        }
        return phone;
    }
}
