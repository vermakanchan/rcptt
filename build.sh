#!/bin/sh
export MAVEN_OPTS="-Xms512m -Xmx1024m -XX:MaxMetaspaceSize=256m -XX:MaxPermSize=256m"

OPTIONS="-Dtycho.localArtifacts=ignore $@"

mvn clean verify -f releng/mirroring/pom.xml $OPTIONS || exit 100 

mvn clean verify -f releng/core/pom.xml $OPTIONS || exit 101
mvn clean verify -f releng/runtime/pom.xml -P runtime3x $OPTIONS || exit 102
mvn clean verify -f releng/runtime/pom.xml -P runtime4x $OPTIONS || exit 103
mvn clean verify -f releng/ide/pom.xml $OPTIONS || exit 105
mvn clean verify -f releng/rcptt/pom.xml $OPTIONS || exit 106
