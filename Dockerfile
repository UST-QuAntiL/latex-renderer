FROM maven:3-jdk-8 as builder

COPY . /tmp/latex-renderer
WORKDIR /tmp/latex-renderer
RUN mvn package -DskipTests

FROM ubuntu:18.04
RUN apt-get update
RUN apt-get install gnupg -y
RUN apt-get install ca-certificates -y
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys D6BC243565B2087BC3F897C9277A7293F59E4889
RUN echo "deb http://miktex.org/download/ubuntu bionic universe" | tee /etc/apt/sources.list.d/miktex.list
RUN apt-get update
RUN apt-get install miktex -y
RUN miktexsetup finish
RUN initexmf --set-config-value [MPM]AutoInstall=1
RUN apt-get update
#miktex admin & package setup
# update file name database
RUN initexmf --update-fndb --admin
# build the font maps
RUN initexmf --mkmaps --admin
# create all possible links
RUN initexmf --mklinks --force --admin
# Check the package repository for updates, then print the list of updateable packages.
RUN mpm --find-updates --admin
# Update all installed packages.
RUN mpm --update --admin

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
