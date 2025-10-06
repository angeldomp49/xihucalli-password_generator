## Installation ##

Maven dependency to include the IOCContainer in your project:

```xml

<dependency>
    <groupId>org.makechtec.software</groupId>
    <artifactId>ioc-container</artifactId>
    <version>3.3.0-RELEASE</version>
</dependency>
```

Gradle version:

```kotlin
implementation("org.makechtec.software:ioc-container:3.0.0-BETA")
```

Groovy version:

```groovy
implementation 'org.makechtec.software:ioc-container:3.0.0-BETA'
```

## USAGE ##

This is an example of how you can create a listener for the application events, then pass it to an application
structure,
and finally start the execution of the application.

```java

public class App {

    static void main(String[] args) {
        var applicationStructure = new ApplicationStructure();
        var applicationListener = new ApplicationEventsListener() {

            @Override
            public Set<BeanInformationEquivalent> onBeansToRegister(EnvironmentContext context) {

                return Set.of(
                        new SingletonInformation(
                                "MySingleton",
                                (context1, container) -> new MySingleton()
                        ),
                        new PrototypeInformation(
                                "MyService",
                                (context1, container, args) -> new MyPrototype(
                                        (MySingleton) container.getSingleton("MySingleton"),
                                        (String) args[0]
                                )
                        )
                );
            }

            @Override
            public void onLoadedApplication(EnvironmentContext context, IOCContainer container) {
                var myPrototype = container.getPrototype("MyService", MyPrototype.class, "TestName");

                assertEquals("TestName", myPrototype.getName());
            }
        };

        applicationStructure.initialize(Set.of(applicationListener));

    }
}
```

## Application Structure ##

This is a simple structured code that creates an EnvironmentContext and initializes the IOCContainer, it also gets
many listeners to dispatch the events about the execution of the application, like the beans to register and when
the application reads properties from a file. These are the allowed listener methods that you can implement:

    void onReadProperties(EnvironmentContext context, JarPropertiesLoader propertiesLoader);

    Set<BeanInformationEquivalent> onBeansToRegister(EnvironmentContext context);

    void onBeforeSingletonsCreation(EnvironmentContext context);

    void onLoadedApplication(EnvironmentContext context, IOCContainer container);

## New ways to define beans ##

As we saw before, there are two new ways to define beans, instead of using the BeanInformation class,
you can use the SingletonInformation and PrototypeInformation classes to define singleton and prototype beans
respectively.

These classes provide a more specific way to define beans, and they are used in the same way as the BeanInformation
class.

## New providers ##

The SingletonProvider and PrototypeProvider classes are new ways to define beans that receive the EnvironmentContext
and the IOCContainer as parameters by default.

Here is an example of how to use them:

```java

@Override
public Set<BeanInformationEquivalent> onBeansToRegister(EnvironmentContext context) {

    return Set.of(
            new SingletonInformation(
                    "MySingleton",
                    (context1, container) -> new MySingleton()
            ),
            new PrototypeInformation(
                    "MyService",
                    (context1, container, args) -> new MyPrototype(
                            (MySingleton) container.getSingleton("MySingleton"),
                            (String) args[0]
                    )
            )
    );
}
```

## Jar Properties Loader ##

The JarPropertiesLoader class is a new way to load properties files from the classpath. It is used in the
ApplicationStructure class
to load properties files from the classpath and likely make them available in the EnvironmentContext.

## Overloads for getSingleton and getPrototype ##

The methods `getSingleton` and `getPrototype` now have overloads that allow you to pass the class type as a parameter.
This makes it easier to retrieve beans without needing to cast them explicitly.
