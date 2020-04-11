FROM gitpod/workspace-full
                    
USER gitpod

# Install custom tools, runtime, etc. using apt-get
# For example, the command below would install "bastet" - a command line tetris clone:
#
# RUN sudo apt-get -q update && #     sudo apt-get install -yq bastet && #     sudo rm -rf /var/lib/apt/lists/*
#
# More information: https://www.gitpod.io/docs/config-docker/

# Setup Java 8 JDK
RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh \
             && sdk install java 8.0.242.j9-adpt"
# Clone and compile smcc artefact             
RUN bash -c "cd /tmp && rm -rf mocca && git clone --branch mocca-1.3.30  https://git.egiz.gv.at/git/mocca && cd /tmp/mocca && sed -i 's/http:\/\/repo1\.maven\.org/https:\/\/repo1\.maven\.org/g' pom.xml && mvn validate && cd smcc && mvn install"