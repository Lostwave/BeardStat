package com.tehbeard.beardstat.bukkit.cfg;

import com.tehbeard.beardstat.cfg.InjectConfig;
import java.lang.reflect.Field;

import com.tehbeard.utils.syringe.Injector;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Injects data into fields of an object from a ConfigurationSection, using @InjectConfig
 * annotations
 * 
 * @author James
 * 
 */
public class YamlConfigInjector extends Injector<Object, InjectConfig> {

    private ConfigurationSection section;

    public YamlConfigInjector(ConfigurationSection section) {

        super(InjectConfig.class);
        this.section = section;
    }

    @Override
    protected void doInject(InjectConfig annotation, Object object, Field field) throws IllegalArgumentException,
            IllegalAccessException {
        Object value = this.section.get(annotation.value());
        if(value != null){
            if( field.getType() == String.class ){
                field.set(object.toString(), value);
            }
            else
            {
                field.set(object, value);
            }
        
        }

    }

}