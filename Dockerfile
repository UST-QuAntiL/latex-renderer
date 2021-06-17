FROM maven:3-jdk-11 as builder
# package latex-renderer
COPY . /tmp/latex-renderer
WORKDIR /tmp/latex-renderer
RUN mvn package -DskipTests

FROM ubuntu:21.04
# configure installs to run non interactive
ARG DEBIAN_FRONTEND=noninteractive 
RUN apt-get update
RUN apt-get install gnupg -y
RUN apt-get install ca-certificates -y

# install tzdata with a default timezone
ENV TZ=Europe/Berlin
RUN apt-get install tzdata

# install texlive
RUN apt-get install texlive texlive-latex-extra texlive-luatex texlive-xetex texlive-lang-european -y

#including java requirements
RUN apt-get update && \
	apt-get install -y openjdk-11-jdk && \
	apt-get install -y ant && \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk11-installer;

# Setup JAVA_HOME
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
RUN export JAVA_HOME

COPY --from=builder /tmp/latex-renderer/target/latex-renderer-1.1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
