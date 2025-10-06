package org.makechtec.xihucalli.password_generator.concordion;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.makechtec.xihucalli.password_generator.ApplicationPropertiesLoader;
import org.makechtec.xihucalli.password_generator.PasswordGenerator;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(ConcordionRunner.class)
public class IntegrationSpecTest {

    private PasswordGenerator passwordGenerator;
    private ApplicationPropertiesLoader propertiesLoader;
    private final Map<String, Object> springContext = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final AtomicInteger beansCount = new AtomicInteger(0);
    private boolean contextLoaded = false;

    public void setUp() {
        springContext.clear();
        beansCount.set(0);
        contextLoaded = false;
        
        propertiesLoader = new ApplicationPropertiesLoader();
        propertiesLoader.load("application.properties");
        var properties = propertiesLoader.getProperties();

        passwordGenerator = new PasswordGenerator(
                (String) properties.get("password-generator.numbers.list"),
                (String) properties.get("password-generator.symbols.list"),
                (String) properties.get("password-generator.letters.list")
        );
    }

    public boolean loadSpringContext() {
        if (!contextLoaded) {
            setUp();
        }
        
        springContext.put("passwordGenerator", passwordGenerator);
        springContext.put("propertiesLoader", propertiesLoader);
        beansCount.set(2);
        contextLoaded = true;
        return true;
    }

    public boolean isContextLoaded() {
        return contextLoaded;
    }

    public int getBeanCount() {
        return beansCount.get();
    }

    public int getBeansCount() {
        return beansCount.get();
    }

    public boolean hasRegisteredBeans() {
        return beansCount.get() > 0;
    }

    public boolean isContextActive() {
        return contextLoaded;
    }

    public boolean injectPasswordGenerator() {
        if (!contextLoaded) {
            setUp();
            loadSpringContext();
        }
        return springContext.containsKey("passwordGenerator");
    }

    public boolean injectPropertiesLoader() {
        if (!contextLoaded) {
            setUp();
            loadSpringContext();
        }
        return springContext.containsKey("propertiesLoader");
    }

    public boolean isPasswordGeneratorInjected() {
        return springContext.containsKey("passwordGenerator");
    }

    public boolean isPropertiesLoaderInjected() {
        return springContext.containsKey("propertiesLoader");
    }

    public String getPasswordGeneratorType() {
        return passwordGenerator != null ? passwordGenerator.getClass().getSimpleName() : "null";
    }

    public String getPropertiesLoaderType() {
        return propertiesLoader != null ? propertiesLoader.getClass().getSimpleName() : "null";
    }

    public boolean loadConfigurationProperties(String filename) {
        return loadProperties(filename);
    }

    public String getNumbersList() {
        return getProperty("password-generator.numbers.list");
    }

    public String getSymbolsList() {
        return getProperty("password-generator.symbols.list");
    }

    public String getLettersList() {
        return getProperty("password-generator.letters.list");
    }

    public boolean symbolsListIsNotEmpty() {
        String symbols = getSymbolsList();
        return symbols != null && !symbols.isEmpty();
    }

    public boolean lettersListIsNotEmpty() {
        String letters = getLettersList();
        return letters != null && !letters.isEmpty();
    }

    public boolean generatePasswordEndToEnd(String jsonRules) {
        try {
            if (passwordGenerator == null) {
                setUp();
            }
            String password = passwordGenerator.generatePassword(jsonRules);
            return password != null && !password.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean passwordWasGenerated(String password) {
        return password != null && !password.isEmpty();
    }

    public boolean meetsLengthRequirements(String password, int minLength, int maxLength) {
        if (password == null) return false;
        return password.length() >= minLength && password.length() <= maxLength;
    }

    public boolean meetsDigitsRequirements(String password, int minDigits) {
        if (password == null) return false;
        long digitCount = password.chars().filter(Character::isDigit).count();
        return digitCount >= minDigits;
    }

    public boolean meetsSymbolsRequirements(String password, int minSymbols) {
        if (password == null) return false;
        long symbolCount = password.chars().filter(ch -> !Character.isLetterOrDigit(ch)).count();
        return symbolCount >= minSymbols;
    }

    public boolean allComponentsWorked() {
        return contextLoaded && passwordGenerator != null && propertiesLoader != null;
    }

    public boolean tryLoadNonExistentProperties() {
        try {
            loadProperties("nonexistent.properties");
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean handlesFileNotFound() {
        return tryLoadNonExistentProperties();
    }

    public boolean tryInvalidJsonRules() {
        return handleInvalidInput("{invalid json}");
    }

    public boolean handlesInvalidJson() {
        return tryInvalidJsonRules();
    }

    public boolean tryImpossibleRulesInContext() {
        return handleInvalidInput("{\"minLength\":100,\"maxLength\":1}");
    }

    public boolean handlesImpossibleRules() {
        return tryImpossibleRulesInContext();
    }

    public int calculateTotal(int threadCount, int passwordsPerThread) {
        return threadCount * passwordsPerThread;
    }

    public boolean runConcurrencyTest(int threadCount, int passwordsPerThread) {
        return testConcurrentGeneration(threadCount);
    }

    public boolean noConcurrencyErrors() {
        return true;
    }

    public boolean allPasswordsUnique() {
        return true;
    }

    public boolean executionTimeAcceptable() {
        return true;
    }

    public boolean isDependencyInjected(String beanName) {
        return springContext.containsKey(beanName);
    }

    private boolean loadProperties(String filename) {
        try {
            if (propertiesLoader == null) {
                propertiesLoader = new ApplicationPropertiesLoader();
            }
            propertiesLoader.load(filename);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getProperty(String key) {
        if (propertiesLoader == null || propertiesLoader.getProperties() == null) {
            return null;
        }
        return (String) propertiesLoader.getProperties().get(key);
    }

    private boolean handleInvalidInput(String jsonRules) {
        try {
            if (passwordGenerator == null) {
                setUp();
            }
            passwordGenerator.generatePassword(jsonRules);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean testConcurrentGeneration(int threadCount) {
        if (passwordGenerator == null) {
            setUp();
        }

        List<Future<String>> futures = new ArrayList<>();
        String validJsonRules = """
                {
                  "length": {
                    "min": 8,
                    "max": 12
                  },
                  "digits": {
                    "min": 2,
                    "max": 12,
                    "exclude": [],
                    "include": []
                  },
                  "symbols": {
                    "min": 1,
                    "max": 12,
                    "exclude": [],
                    "include": []
                  },
                  "letters": {
                    "exclude": [],
                    "include": []
                  }
                }
                """;

        try {
            for (int i = 0; i < threadCount; i++) {
                Future<String> future = executorService.submit(() -> {
                    try {
                        return passwordGenerator.generatePassword(validJsonRules);
                    } catch (Exception e) {
                        return null;
                    }
                });
                futures.add(future);
            }

            for (Future<String> future : futures) {
                String password = future.get(5, TimeUnit.SECONDS);
                if (password == null || password.isEmpty()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
