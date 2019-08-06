# Keycloak Connector
Generates client jar for interacting with keycloak architecture.
This repository consists of two modules, 
1. The core module with the keycloak-connector logic itself
2. A testutils module with some utilities to use with Spring Test

# Core Module

## Installation
Just add the dependency on your `pom.xml` file

```
<dependency>
    <groupId>org.entando</groupId>
    <artifactId>keycloak-connector</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Configuration
Add these configurations to your `application.properties` file

### To enable Keycloak security
```
keycloak.enabled=true
keycloak.auth-server-url=${KEYCLOAK_AUTH_URL:http://keycloak.url.here/auth}
keycloak.realm=${KEYCLOAK_REALM:realm-here}
keycloak.resource=${KEYCLOAK_CLIENT_ID:client-id}
keycloak.credentials.secret=${KEYCLOAK_CLIENT_SECRET}
keycloak.ssl-required=external
keycloak.public-client=false
```

### Custom Keycloak configurations

>- `entando.keycloak.sessionStateful`: Mark as true if you want to use redirect and session directly through Java. Default: `false`

### Exceptions
This project throw two exceptions.

#### org.entando.keycloak.ForbiddenException
Thrown when the user doesn't have rights to execute an operation.

#### org.entando.keycloak.UnauthorizedException
Thrown when no credential is provided on a secured endpoint.

### Resolvers
It's possible to inject the authenticated user to the controller you are developing.
Just add `AuthenticatedUser` argument to your endpoint method.

# Test utilities module
This modules contains some utilities class to enable integration tests when using keycloak-connector

## Installation
Just add the dependency on your `pom.xml` file

```
<dependency>
    <groupId>org.entando</groupId>
    <artifactId>keycloak-connector-testutils</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

## Configuration
Add these configurations to your `application.properties` file

### To enable Keycloak security
```
keycloak.enabled=true
keycloak.auth-server-url=${KEYCLOAK_AUTH_URL:http://keycloak.url.here/auth}
keycloak.realm=${KEYCLOAK_REALM:realm-here}
keycloak.resource=${KEYCLOAK_CLIENT_ID:client-id}
keycloak.credentials.secret=${KEYCLOAK_CLIENT_SECRET}
keycloak.ssl-required=external
keycloak.public-client=false
```


## Usage with Spring MockMvc
Here you can find an example of how to use the `@WithMockKeycloakUser` in a SpringBoot integration test.
Please note that in order for Spring to beign able to use the annotation, you will need to setup the mockMvc as shown in the
`setup()` method.

For more details on how to leverage Spring Boot Test, please refer to the [Spring documentation](https://docs.spring.io/spring-security/site/docs/current/reference/html/test.html)

```java
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.entando.keycloak.testutils.WithMockKeycloakUser;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AvatarPluginApp.class)
public class MyCustomTests {


    @Autowired
    private WebApplicationContext applicationContext;

    private MockMvc myMockMvc;

    @Before
    public void setup() {
        myMockMvc = MockMvcBuilders
            .webAppContextSetup(applicationContext)
            .apply(springSecurity())
            .build();
    }

    @Test
    @WithMockKeycloakUser(username="keycloak-user", roles={"get-component","post-component"}, resource="entando-plugin-a")
    public void mySpecialTest() {
        // your test code here
    }

```

**Note**: If no resource is provided in the `resource` field of the `@WithMockKeycloakUser`, by default the resource is set to the `keycloak.resource` defined in the configuration file
