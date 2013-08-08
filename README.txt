Plagin use example

<plugin>
    <groupId>com.degas.plugins</groupId>
    <artifactId>javac2-maven-plugin</artifactId>
    <version>3.0</version>
    <executions>
        <execution>
            <id>@NotNull Instrumentation</id>
            <goals>
                <goal>run</goal>
            </goals>
            <phase>compile</phase>
        </execution>
    </executions>
    <configuration>
        <fork>true</fork>
    </configuration>
</plugin>

<dependency>
    <groupId>com.degas.plugins</groupId>
    <artifactId>javac2-maven-plugin</artifactId>
    <version>3.0</version>
    <scope>provided</scope>
</dependency>
