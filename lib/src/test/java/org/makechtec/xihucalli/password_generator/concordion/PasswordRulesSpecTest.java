package org.makechtec.xihucalli.password_generator.concordion;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.makechtec.xihucalli.password_generator.ApplicationPropertiesLoader;
import org.makechtec.xihucalli.password_generator.PasswordRulesInformation;

import java.util.*;

@RunWith(ConcordionRunner.class)
public class PasswordRulesSpecTest {

    private PasswordRulesInformation passwordRules;
    private ApplicationPropertiesLoader propertiesLoader;

    public void setUp() {
        passwordRules = new PasswordRulesInformation();
        propertiesLoader = new ApplicationPropertiesLoader();
    }

    public void createBasicRules(long minLength, long maxLength, long minDigits, long minSymbols) {
        if (passwordRules == null) {
            setUp();
        }
        passwordRules.setMinLength(minLength);
        passwordRules.setMaxLength(maxLength);
        passwordRules.setMinNumberOfDigits(minDigits);
        passwordRules.setMinNumberOfSymbols(minSymbols);
    }

    public long getMinLength() {
        return passwordRules.getMinLength();
    }

    public long getMaxLength() {
        return passwordRules.getMaxLength();
    }

    public long getMinDigits() {
        return passwordRules.getMinNumberOfDigits();
    }

    public long getMinSymbols() {
        return passwordRules.getMinNumberOfSymbols();
    }

    public void setIncludedDigits(List<Integer> digits) {
        if (passwordRules == null) {
            setUp();
        }
        passwordRules.setIncludedDigits(digits);
    }

    public void setIncludedSymbols(List<Character> symbols) {
        if (passwordRules == null) {
            setUp();
        }
        passwordRules.setIncludedSymbols(symbols);
    }

    public List<Integer> getIncludedDigits() {
        return passwordRules.getIncludedDigits();
    }

    public List<Character> getIncludedSymbols() {
        return passwordRules.getIncludedSymbols();
    }

    public void setExcludedDigits(List<Integer> digits) {
        if (passwordRules == null) {
            setUp();
        }
        passwordRules.setExcludedDigits(digits);
    }

    public void setExcludedSymbols(List<Character> symbols) {
        if (passwordRules == null) {
            setUp();
        }
        passwordRules.setExcludedSymbols(symbols);
    }

    public List<Integer> getExcludedDigits() {
        return passwordRules.getExcludedDigits();
    }

    public List<Character> getExcludedSymbols() {
        return passwordRules.getExcludedSymbols();
    }

    public boolean isValidConfiguration() {
        return passwordRules.getMinLength() <= passwordRules.getMaxLength() &&
               passwordRules.getMinNumberOfDigits() >= 0 &&
               passwordRules.getMinNumberOfSymbols() >= 0;
    }

    public void loadFromProperties(String filename) {
        if (propertiesLoader == null) {
            setUp();
        }
        propertiesLoader.load(filename);
    }

    public boolean hasProperties() {
        return propertiesLoader != null && propertiesLoader.getProperties() != null && 
               !propertiesLoader.getProperties().isEmpty();
    }
}
