package br.com.rooting.liquid;

import br.com.rooting.liquid.model.LiquidObject;
import br.com.rooting.liquid.model.LiquidProperty;

public final class LiquidObjectFormatter {

    private LiquidObjectFormatter() {}

    public static String formatAsJavaDeclaration(LiquidObject liquidObject) {
        StringBuilder builder = new StringBuilder();
        builder.append("List<LiquidProperty> properties = new ArrayList<>();\n");

        liquidObject.asList().forEach(p -> builder.append("properties.add(").append(formatAsJavaDeclaration(p)).append(");\n"));
        builder.append("LiquidObject liquidObject = LiquidObject.create(properties);");
        return builder.toString();
    }

    public static String formatAsJavaDeclaration(LiquidProperty property) {
        StringBuilder orderStringBuilder = new StringBuilder();
        orderStringBuilder.append("Arrays.asList(");

        orderStringBuilder.append(")");
        return "new LiquidProperty(\""+ property.getKey() + "\", \"" + property.getValue() + "\")";
    }

}
