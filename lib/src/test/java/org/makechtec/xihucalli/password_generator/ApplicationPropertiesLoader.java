package org.makechtec.xihucalli.password_generator;

import java.io.IOException;
import java.util.Properties;

public class ApplicationPropertiesLoader {
    
    private final Properties properties;
    
    public ApplicationPropertiesLoader(){
        properties = new Properties();
    }
    
    public void load(String propertiesFile) {
        
        try(var applicationPropertiesFile = ApplicationPropertiesLoader.class.getClassLoader().getResourceAsStream(propertiesFile)) {
            
            properties.load(applicationPropertiesFile);
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Properties getProperties() {
        return properties;
    }
    
}
