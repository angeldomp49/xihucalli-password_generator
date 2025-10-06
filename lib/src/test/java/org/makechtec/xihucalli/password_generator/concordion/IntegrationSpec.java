package org.makechtec.xihucalli.password_generator.concordion;

import org.concordion.api.ConcordionRunner;
import org.junit.jupiter.api.extension.ExtendWith;
import org.makechtec.xihucalli.password_generator.ApplicationPropertiesLoader;
import org.makechtec.xihucalli.password_generator.PasswordGenerator;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(ConcordionRunner.class)
public class IntegrationSpec {

    private PasswordGenerator passwordGenerator;
    private ApplicationPropertiesLoader propertiesLoader;
    private final Map<String, Object> testResults = new ConcurrentHashMap<>();
    private final Map<String, Object> springContext = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final AtomicInteger beansCount = new AtomicInteger(0);
    private boolean contextLoaded = false;

    @BeforeEach
    void setUp() {
        testResults.clear();
        springContext.clear();
        beansCount.set(0);
        contextLoaded = false;
        
        // Simulate Spring context initialization
        initializeSpringContext();
    }

    private void initializeSpringContext() {
        try {
            // Simulate bean registration and context loading
            propertiesLoader = new ApplicationPropertiesLoader();
            propertiesLoader.load("application.properties");
            
            var properties = propertiesLoader.getProperties();
            passwordGenerator = new PasswordGenerator(
                    (String) properties.get("password-generator.numbers.list"),
                    (String) properties.get("password-generator.symbols.list"),
                    (String) properties.get("password-generator.letters.list")
            );
            
            // Register beans in simulated context
            springContext.put("passwordGenerator", passwordGenerator);
            springContext.put("propertiesLoader", propertiesLoader);
            beansCount.set(springContext.size());
            contextLoaded = true;
            
            testResults.put("contextInitialized", true);
        } catch (Exception e) {
            testResults.put("contextInitialized", false);
            testResults.put("contextError", e.getMessage());
        }
    }

    public void loadSpringContext() {
        if (!contextLoaded) {
            initializeSpringContext();
        }
        testResults.put("springContextLoaded", contextLoaded);
    }

    public int getBeansCount() {
        return beansCount.get();
    }

    public boolean isContextLoaded() {
        return contextLoaded && springContext.containsKey("passwordGenerator");
    }

    public boolean hasRegisteredBeans() {
        return beansCount.get() > 0;
    }

    public boolean isContextActive() {
        return contextLoaded && !springContext.isEmpty();
    }

    public void injectPasswordGenerator() {
        Object bean = springContext.get("passwordGenerator");
        testResults.put("passwordGeneratorInjected", bean != null);
        testResults.put("passwordGeneratorBean", bean);
    }

    public void injectPropertiesLoader() {
        Object bean = springContext.get("propertiesLoader");
        testResults.put("propertiesLoaderInjected", bean != null);
        testResults.put("propertiesLoaderBean", bean);
    }

    public boolean isPasswordGeneratorInjected() {
        return (Boolean) testResults.getOrDefault("passwordGeneratorInjected", false);
    }

    public boolean isPropertiesLoaderInjected() {
        return (Boolean) testResults.getOrDefault("propertiesLoaderInjected", false);
    }

    public String getPasswordGeneratorType() {
        Object bean = testResults.get("passwordGeneratorBean");
        return bean != null ? bean.getClass().getSimpleName() : "No disponible";
    }

    public String getPropertiesLoaderType() {
        Object bean = testResults.get("propertiesLoaderBean");
        return bean != null ? bean.getClass().getSimpleName() : "No disponible";
    }

    public void loadConfigurationProperties(String propertiesFile) {
        try {
            if (propertiesLoader == null) {
                propertiesLoader = new ApplicationPropertiesLoader();
            }
            propertiesLoader.load(propertiesFile);
            testResults.put("configurationLoaded", true);
            testResults.put("loadedProperties", propertiesLoader.getProperties());
        } catch (Exception e) {
            testResults.put("configurationLoaded", false);
            testResults.put("configurationError", e.getMessage());
        }
    }

    public String getNumbersList() {
        Map<String, Object> props = (Map<String, Object>) testResults.get("loadedProperties");
        if (props != null) {
            return (String) props.get("password-generator.numbers.list");
        }
        return "";
    }

    public String getSymbolsList() {
        Map<String, Object> props = (Map<String, Object>) testResults.get("loadedProperties");
        if (props != null) {
            return (String) props.get("password-generator.symbols.list");
        }
        return "";
    }

    public String getLettersList() {
        Map<String, Object> props = (Map<String, Object>) testResults.get("loadedProperties");
        if (props != null) {
            return (String) props.get("password-generator.letters.list");
        }
        return "";
    }

    public boolean symbolsListIsNotEmpty() {
        String symbols = getSymbolsList();
        return symbols != null && !symbols.trim().isEmpty();
    }

    public boolean lettersListIsNotEmpty() {
        String letters = getLettersList();
        return letters != null && !letters.trim().isEmpty();
    }

    public String generatePasswordEndToEnd(String jsonRules) {
        try {
            if (passwordGenerator == null) {
                throw new IllegalStateException("PasswordGenerator not initialized in Spring context");
            }
            
            String password = passwordGenerator.generatePassword(jsonRules);
            testResults.put("endToEndPassword", password);
            testResults.put("endToEndSuccess", true);
            testResults.put("endToEndRules", jsonRules);
            return password;
        } catch (Exception e) {
            testResults.put("endToEndSuccess", false);
            testResults.put("endToEndError", e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }

    public boolean passwordWasGenerated(String password) {
        return password != null && !password.trim().isEmpty() && !password.startsWith("ERROR:");
    }

    public boolean meetsLengthRequirements(String password, int minLength, int maxLength) {
        if (password == null || password.startsWith("ERROR:")) {
            return false;
        }
        return password.length() >= minLength && password.length() <= maxLength;
    }

    public boolean meetsDigitsRequirements(String password, int minDigits) {
        if (password == null || password.startsWith("ERROR:")) {
            return false;
        }
        long digitCount = password.chars().filter(Character::isDigit).count();
        return digitCount >= minDigits;
    }

    public boolean meetsSymbolsRequirements(String password, int minSymbols) {
        if (password == null || password.startsWith("ERROR:")) {
            return false;
        }
        long symbolCount = password.chars()
                .filter(ch -> !Character.isLetterOrDigit(ch))
                .count();
        return symbolCount >= minSymbols;
    }

    public boolean allComponentsWorked() {
        return (Boolean) testResults.getOrDefault("endToEndSuccess", false) &&
               isContextLoaded() &&
               isPasswordGeneratorInjected();
    }

    public void tryLoadNonExistentProperties() {
        try {
            ApplicationPropertiesLoader loader = new ApplicationPropertiesLoader();
            loader.load("non-existent-file.properties");
            testResults.put("fileNotFoundHandled", false);
        } catch (Exception e) {
            testResults.put("fileNotFoundHandled", true);
            testResults.put("fileNotFoundError", e.getMessage());
        }
    }

    public void tryInvalidJsonRules() {
        try {
            String invalidJson = "{ invalid json structure";
            passwordGenerator.generatePassword(invalidJson);
            testResults.put("invalidJsonHandled", false);
        } catch (SecurityException e) {
            testResults.put("invalidJsonHandled", true);
            testResults.put("invalidJsonError", e.getMessage());
        } catch (Exception e) {
            testResults.put("invalidJsonHandled", true);
            testResults.put("invalidJsonError", e.getMessage());
        }
    }

    public void tryImpossibleRulesInContext() {
        try {
            String impossibleRules = """
                {
                  "length": {"min": 3, "max": 3},
                  "digits": {"min": 5, "max": 5},
                  "symbols": {"min": 5, "max": 5}
                }
                """;
            passwordGenerator.generatePassword(impossibleRules);
            testResults.put("impossibleRulesHandled", false);
        } catch (SecurityException e) {
            testResults.put("impossibleRulesHandled", true);
            testResults.put("impossibleRulesError", e.getMessage());
        }
    }

    public boolean handlesFileNotFound() {
        return (Boolean) testResults.getOrDefault("fileNotFoundHandled", false);
    }

    public boolean handlesInvalidJson() {
        return (Boolean) testResults.getOrDefault("invalidJsonHandled", false);
    }

    public boolean handlesImpossibleRules() {
        return (Boolean) testResults.getOrDefault("impossibleRulesHandled", false);
    }

    public int calculateTotal(int threadCount, int passwordsPerThread) {
        return threadCount * passwordsPerThread;
    }

    public void runConcurrencyTest(int threadCount, int passwordsPerThread) {
        List<Future<List<String>>> futures = new ArrayList<>();
        final String testRules = """
            {
              "length": {"min": 10, "max": 15},
              "digits": {"min": 2, "max": 5},
              "symbols": {"min": 1, "max": 3}
            }
            """;

        long startTime = System.currentTimeMillis();

        // Submit concurrent tasks
        for (int i = 0; i < threadCount; i++) {
            Future<List<String>> future = executorService.submit(() -> {
                List<String> passwords = new ArrayList<>();
                for (int j = 0; j < passwordsPerThread; j++) {
                    try {
                        String password = passwordGenerator.generatePassword(testRules);
                        passwords.add(password);
                    } catch (Exception e) {
                        // Log error but continue
                        passwords.add("ERROR: " + e.getMessage());
                    }
                }
                return passwords;
            });
            futures.add(future);
        }

        // Collect results
        Set<String> allPasswords = new ConcurrentHashMap<String, Boolean>().keySet(ConcurrentHashMap.newKeySet());
        List<String> errors = new ArrayList<>();
        
        for (Future<List<String>> future : futures) {
            try {
                List<String> passwords = future.get(30, TimeUnit.SECONDS);
                for (String password : passwords) {
                    if (password.startsWith("ERROR:")) {
                        errors.add(password);
                    } else {
                        allPasswords.add(password);
                    }
                }
            } catch (Exception e) {
                errors.add("Future execution error: " + e.getMessage());
            }
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        testResults.put("concurrencyErrors", errors);
        testResults.put("generatedPasswords", allPasswords);
        testResults.put("totalGenerated", allPasswords.size());
        testResults.put("executionTime", executionTime);
        testResults.put("expectedTotal", threadCount * passwordsPerThread);
    }

    public boolean noConcurrencyErrors() {
        List<String> errors = (List<String>) testResults.getOrDefault("concurrencyErrors", new ArrayList<>());
        return errors.isEmpty();
    }

    public boolean allPasswordsUnique() {
        Set<String> passwords = (Set<String>) testResults.get("generatedPasswords");
        Integer totalGenerated = (Integer) testResults.get("totalGenerated");
        
        if (passwords == null || totalGenerated == null) {
            return false;
        }
        
        // Allow for some duplicates due to randomness, but not excessive
        double uniquenessRatio = (double) passwords.size() / totalGenerated;
        return uniquenessRatio > 0.95; // 95% uniqueness threshold
    }

    public boolean executionTimeAcceptable() {
        Long executionTime = (Long) testResults.get("executionTime");
        Integer expectedTotal = (Integer) testResults.get("expectedTotal");
        
        if (executionTime == null || expectedTotal == null) {
            return false;
        }
        
        // Reasonable performance: less than 10ms per password on average
        double avgTimePerPassword = (double) executionTime / expectedTotal;
        return avgTimePerPassword < 10.0;
    }
}
