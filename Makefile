.PHONY: all server client run-server run-clients clean

SERVER_PORT ?= 12345

SERVER_JAR := SERVER/build/libs/SERVER.jar
CLIENT_JAR := CLIENT/build/libs/client.jar

all: server client

server: $(SERVER_JAR)

$(SERVER_JAR):
	@echo "Building server application..."
	cd SERVER && ./gradlew build -x test

client: $(CLIENT_JAR)

$(CLIENT_JAR):
	@echo "Building client application..."
	cd CLIENT && ./gradlew build -x test

run-server: server
	@echo "Starting server on port $(SERVER_PORT)..."
	cd SERVER && ./run-server.sh $(SERVER_PORT)

run-clients: server client
	@echo "Starting 100 clients against server on port $(SERVER_PORT)..."
	cd CLIENT && ./run-clients.sh $(SERVER_PORT)

clean:
	@echo "Cleaning build artifacts..."
	cd SERVER && ./gradlew clean
	cd CLIENT && ./gradlew clean
