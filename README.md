# waltz-war-plugin
Template repo for Waltz war plugin

## Compilation

In case you wish to compile this project you will require several things:

* Github Token (Settings -> Developer Settings -> Personal Access Tokens -> New token with permissions for packages)
* Installed `Maven` (`sudo apt install -y maven` for ubuntu-like packages)
* Configured `~/.m2/settings.xml`

### settings.xml

Your `settings.xml` must looks like:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>Ingvord</id>
          <name>GitHub Ingvord Apache Maven Packages</name>
          <url>https://maven.pkg.github.com/Ingvord/RxJTango</url>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>Ingvord</id>
      <username>YOUR GITHUB USERNAME</username>
      <password>GITHUB TOKEN</password>
    </server>
  </servers>
</settings>
```

### NPM error

In case you have faced with next error:

```bash
npm ERR! code EJSONPARSE
npm ERR! file /home/aleksey/projects/study/magix-tango-connector/package.json
npm ERR! JSON.parse Failed to parse json
npm ERR! JSON.parse Unexpected token 
npm ERR! JSON.parse  in JSON at position 873 while parsing '{
npm ERR! JSON.parse   "name": "SET PACKAGE NAME",
npm ERR! JSON.parse   "versi'
npm ERR! JSON.parse Failed to parse package.json data.
npm ERR! JSON.parse package.json must be actual JSON, not just JavaScript.
```

All you need to do for successful compilation is just to comment next lines in `pom.xml`:

```xml
<execution>
    <id>generate-js</id>
    <phase>generate-sources</phase>
    <goals>
        <goal>exec</goal>
    </goals>
    <configuration>
        <executable>npm</executable>
        <arguments>
            <argument>run</argument>
            <argument>build</argument>
        </arguments>
    </configuration>
</execution>
```

Or just replace with next xml part:


```xml
<!--                    <execution>-->
<!--                        <id>generate-js</id>-->
<!--                        <phase>generate-sources</phase>-->
<!--                        <goals>-->
<!--                            <goal>exec</goal>-->
<!--                        </goals>-->
<!--                        <configuration>-->
<!--                            <executable>npm</executable>-->
<!--                            <arguments>-->
<!--                                <argument>run</argument>-->
<!--                                <argument>build</argument>-->
<!--                            </arguments>-->
<!--                        </configuration>-->
<!--                    </execution>-->
```
