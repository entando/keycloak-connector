# Keycloak Connector
Generates client jar for interacting with keycloak architecture

# Installation
Just add the dependency on your `pom.xml` file

```
<dependency>
    <groupId>com.entando</groupId>
    <artifactId>keycloak-connector</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

# Configuration
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

#### com.entando.keycloak.ForbiddenException
Thrown when the user doesn't have rights to execute an operation.

#### com.entando.keycloak.UnauthorizedException
Thrown when no credential is provided on a secured endpoint.

### Resolvers
It's possible to inject the authenticated user to the controller you are developing.
Just add `AuthenticatedUser` argument to your endpoint method.