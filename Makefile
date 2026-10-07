.PHONY: compile test package clean

clean:
	mvn clean

compile:
	mvn compile

test:
	mvn test

# Tests run in their own pipeline stage, so packaging does not run them again.
package:
	mvn package -DskipTests
	java -jar target/scootshare-jar-with-dependencies.jar
