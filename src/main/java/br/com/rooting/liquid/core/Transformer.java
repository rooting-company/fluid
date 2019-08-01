package br.com.rooting.liquid.core;

import br.com.rooting.liquid.config.Config;
import br.com.rooting.liquid.core.liquid.Liquifier;
import br.com.rooting.liquid.core.solid.Solidifier;
import br.com.rooting.liquid.model.LiquidObject;

public class Transformer {

    private Config config;

    public Transformer(Config config) {
        this.config = config;
    }

    public LiquidObject liquify(Object object) {
        assert object != null;

        Liquifier liquifier = new Liquifier(object, config);
        return liquifier.liquify();
   }

   public <T> T solidify(LiquidObject liquidObject, Class<T> type) {
        assert type != null;
        assert liquidObject != null;

        Solidifier<T> solidifier = new Solidifier<>(type, liquidObject, config);
        return solidifier.solidify();
   }

}
