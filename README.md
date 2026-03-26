# Proyecto Java + Oracle Autonomous Database (Wallet)

## Descripción

Este proyecto permite la conexión de una aplicación Java a una base de datos en Oracle Cloud (Autonomous Database) utilizando JDBC y autenticación mediante Wallet.

---

## Requisitos

Antes de empezar, asegurarse de tener instalado:

* Java JDK 17 (recomendado Oracle JDK)
* Apache Maven
* NetBeans / IntelliJ / Eclipse
* Conexión a internet

---

## 1. Descargar el Wallet

1. Ingresar a Oracle Cloud
2. Ir a tu Autonomous Database
3. Click en **DB Connection**
4. Click en **Download Wallet**
5. Asignar una contraseña
6. Descargar el archivo `.zip`

---

## 2. Configurar el Wallet

1. Crear carpeta:

```
C:/Wallet
```

2. Extraer el contenido del `.zip` dentro de esa carpeta

Estructura esperada:

```
C:/Wallet/
 ├── tnsnames.ora
 ├── sqlnet.ora
 ├── cwallet.sso
 ├── ewallet.p12
```

---

## 3. Verificar configuración

Abrir:

```
C:/Wallet/sqlnet.ora
```

Debe contener:

```
WALLET_LOCATION = (SOURCE = (METHOD = file) (METHOD_DATA = (DIRECTORY=C:/Wallet)))
SSL_SERVER_DN_MATCH=yes
```

---

## 4. Configurar variables de entorno (.env)

Crear un archivo .env en la raíz del proyecto:
```
DB_USER=usuario
DB_PASSWORD=contraseña
DB_URL=jdbc:oracle:thin:@nombre_servicio
WALLET_PATH=C:/Wallet
```

###### IMPORTANTE:

Nunca subir este archivo a GitHub

Agregar al .gitignore:

.env

---

## 5. Configurar dependencias (Maven)

Agregar en `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>com.oracle.database.jdbc</groupId>
        <artifactId>ojdbc11</artifactId>
        <version>23.3.0.23.09</version>
    </dependency>

    <dependency>
        <groupId>com.oracle.database.security</groupId>
        <artifactId>oraclepki</artifactId>
        <version>23.3.0.23.09</version>
    </dependency>

    <!-- Para leer .env --> 
    <dependency> 
        <groupId>io.github.cdimascio</groupId> 
        <artifactId>java-dotenv</artifactId> 
        <version>5.2.2</version> 
    </dependency>
</dependencies>
```
### Resultado del xml completo
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>com</groupId>
    <artifactId>ProyectoFinalLenguajes</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.release>17</maven.compiler.release>
        <exec.mainClass>com.proyectofinallenguajes.ProyectoFinalLenguajes</exec.mainClass>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.oracle.database.jdbc</groupId>
            <artifactId>ojdbc11</artifactId>
            <version>23.3.0.23.09</version>
        </dependency>
        <dependency>
            <groupId>com.oracle.database.security</groupId>
            <artifactId>oraclepki</artifactId>
            <version>23.3.0.23.09</version>
        </dependency>
        <!-- Para leer .env --> 
        <dependency> 
            <groupId>io.github.cdimascio</groupId> 
            <artifactId>java-dotenv</artifactId> 
            <version>5.2.2</version> 
        </dependency>
    </dependencies>
</project>
```

---

## 6. Código de conexión

```java

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProyectoFinalLenguajes {
    
    public static void main(String[] args) {
        System.out.println(System.getProperty("java.version"));
        System.out.println(System.getProperty("java.home"));

        try {
            
            //Cargar variables del archivo .env
            Dotenv dotenv = Dotenv.load();
            
            String user = dotenv.get("DB_USER"); 
            String password = dotenv.get("DB_PASSWORD"); 
            String url = dotenv.get("DB_URL"); 
            String walletPath = dotenv.get("WALLET_PATH");
            
            System.setProperty("oracle.net.tns_admin", walletPath);

            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", password);

            Connection conn = DriverManager.getConnection(url, properties);
            
            System.out.println("Connection successfull!");
            
            conn.close();            

        } catch (SQLException e) {
            System.out.println("Connection failed:");
            System.out.println("Message: " + e.getMessage());
            System.out.println("Code: " + e.getErrorCode());
            e.printStackTrace();
        }
    }
}
```

---

## 7. Ejecutar el proyecto

Con Maven:

```bash
Ejecutar desde el IDE.
```

---

## Resultado esperado

```
Connection successful!
```

---

## Problemas comunes

### ORA-17957: Unable to initialize the key store

**Causa:**

* Falta dependencia `oraclepki`
* Wallet mal configurado

**Solución:**

* Verificar `pom.xml`
* Revisar `sqlnet.ora`
* Confirmar ruta `C:/Wallet`

---

### SSO KeyStore not available

**Causa:**

* Falta soporte de seguridad

**Solución:**

* Usar JDK 17
* Agregar `oraclepki`

---

### No conecta

**Causa:**

* Wallet incorrecto

**Solución:**

* Descargar nuevamente el wallet desde Oracle Cloud

---

## Notas importantes

* No subir el wallet al repositorio
* No compartir credenciales
* Usar rutas absolutas

---

## Autores

* Luis Salas
* Fabian Solano
* Eduardo Ramos