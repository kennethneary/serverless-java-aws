all: build

build: src/main/java/com/serverless/handler/*.java
	mvn package

clean:
	mvn clean

deploy: build
	serverless deploy