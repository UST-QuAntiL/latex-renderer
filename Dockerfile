FROM maven:3-jdk-8 as builder

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

RUN apt-get install pdf2svg -y &&\
        apt-get install poppler-utils -y && \
        apt-get install libcairo2-dev -y && \
        apt-get install nano;

#including picoded/ubuntu-openjdk-8-jdk
RUN apt-get update && \
	apt-get install -y openjdk-8-jdk && \
	apt-get install -y ant && \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk8-installer;

# Fix certificate issues, found as of
# https://bugs.launchpad.net/ubuntu/+source/ca-certificates-java/+bug/983302
RUN apt-get update && \
	apt-get install -y ca-certificates-java && \
	apt-get clean && \
	update-ca-certificates -f && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk8-installer;


# Setup JAVA_HOME, this is useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME




COPY --from=builder /tmp/latex-renderer/target/api-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
