package com.tehbeard.beardstat.dataproviders;

import com.tehbeard.utils.syringe.configInjector.InjectConfig;
import java.lang.reflect.Field;
import java.util.Properties;
import me.tehbeard.utils.syringe.Injector;

/**
 *
 * @author James
 */
public class JavaPropertiesInjector extends Injector<Object, InjectConfig> {

    Properties prop;

    public JavaPropertiesInjector(Properties prop) {
        super(InjectConfig.class);
        this.prop = prop;
    }

    @Override
    protected void doInject(InjectConfig annotation, Object object, Field field) throws IllegalArgumentException, IllegalAccessException {
        Object value = prop.get(annotation.value());
        if (value != null) {
            if(int.class.isAssignableFrom(field.getType())){
                field.set(object, Integer.parseInt("" + value));
            }
            field.set(object, value);
            
        }
    }
}
