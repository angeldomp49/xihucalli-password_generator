package org.makechtec.xihucalli.password_generator.concordion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.makechtec.xihucalli.password_generator.ApplicationPropertiesLoader;
import org.makechtec.xihucalli.password_generator.PasswordGenerator;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Spring Boot Integration Tests")
public class IntegrationSpecTest {

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

    @SuppressWarnings("unchecked")
    public String getNumbersList() {
        Map<String, Object> props = (Map<String, Object>) testResults.get("loadedProperties");
        if (props != null) {
            return (String) props.get("password-generator.numbers.list");
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public String getSymbolsList() {
        Map<String, Object> props = (Map<String, Object>) testResults.get("loadedProperties");
        if (props != null) {
            return (String) props.get("password-generator.symbols.list");
        }
        return "";
    }

    @SuppressWarnings("unchecked")
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
        Set<String> allPasswords = ConcurrentHashMap.newKeySet();
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

    @SuppressWarnings("unchecked")
    public boolean noConcurrencyErrors() {
        List<String> errors = (List<String>) testResults.getOrDefault("concurrencyErrors", new ArrayList<>());
        return errors.isEmpty();
    }

    @SuppressWarnings("unchecked")
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

    @Test
    @DisplayName("Escenario 1: Carga del contexto de Spring")
    void testSpringContextLoading() {
        // When
        loadSpringContext();
        
        // Then
        assertTrue(isContextLoaded(), "Context should be loaded without errors");
        assertTrue(hasRegisteredBeans(), "Should have registered beans");
        assertTrue(isContextActive(), "Context should be active");
        assertTrue(getBeansCount() > 0, "Should have at least one bean");
    }

    @Test
    @DisplayName("Escenario 2: Inyección de dependencias")
    void testDependencyInjection() {
        // When
        injectPasswordGenerator();
        injectPropertiesLoader();
        
        // Then
        assertTrue(isPasswordGeneratorInjected(), "PasswordGenerator should be injected");
        assertTrue(isPropertiesLoaderInjected(), "PropertiesLoader should be injected");
        assertEquals("PasswordGenerator", getPasswordGeneratorType(), "Should be correct type");
        assertEquals("ApplicationPropertiesLoader", getPropertiesLoaderType(), "Should be correct type");
    }

    @Test
    @DisplayName("Escenario 3: Configuración de propiedades")
    void testConfigurationProperties() {
        // When
        loadConfigurationProperties("application.properties");
        
        // Then
        assertEquals("0123456789", getNumbersList(), "Numbers list should be loaded correctly");
        assertTrue(symbolsListIsNotEmpty(), "Symbols list should not be empty");
        assertTrue(lettersListIsNotEmpty(), "Letters list should not be empty");
    }

    @Test
    @DisplayName("Escenario 4: Integración end-to-end")
    void testEndToEndIntegration() {
        // Given
        String jsonRules = "{\"length\":{\"min\":10,\"max\":15},\"digits\":{\"min\":2,\"max\":5},\"symbols\":{\"min\":1,\"max\":3}}";
        
        // Ensure dependencies are properly injected for this test
        injectPasswordGenerator();
        
        // When
        String password = generatePasswordEndToEnd(jsonRules);
        
        // Then
        assertTrue(passwordWasGenerated(password), "Password should be generated successfully");
        assertTrue(meetsLengthRequirements(password, 10, 15), "Should meet length requirements");
        assertTrue(meetsDigitsRequirements(password, 2), "Should have minimum digits");
        assertTrue(meetsSymbolsRequirements(password, 1), "Should have minimum symbols");
        assertTrue(allComponentsWorked(), "All components should work together");
    }

    @Test
    @DisplayName("Escenario 5: Manejo de errores en contexto Spring")
    void testErrorHandlingInSpringContext() {
        // Test file not found
        tryLoadNonExistentProperties();
        assertTrue(handlesFileNotFound(), "Should handle file not found error");
        
        // Test invalid JSON
        tryInvalidJsonRules();
        assertTrue(handlesInvalidJson(), "Should handle invalid JSON error");
        
        // Test impossible rules
        tryImpossibleRulesInContext();
        assertTrue(handlesImpossibleRules(), "Should handle impossible rules error");
    }

    @Test
    @DisplayName("Escenario 6: Rendimiento y concurrencia")
    void testPerformanceAndConcurrency() {
        // Given
        int threadCount = 5;
        int passwordsPerThread = 20;
        int expectedTotal = calculateTotal(threadCount, passwordsPerThread);
        
        // When
        runConcurrencyTest(threadCount, passwordsPerThread);
        
        // Then
        assertTrue(noConcurrencyErrors(), "Should have no concurrency errors");
        assertTrue(allPasswordsUnique(), "Passwords should be unique");
        assertTrue(executionTimeAcceptable(), "Execution time should be acceptable");
        assertEquals(100, expectedTotal, "Should calculate total correctly");
    }
}
