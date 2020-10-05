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
