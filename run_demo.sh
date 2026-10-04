#!/usr/bin/env bash
# Lance le jeu complet (Mac / Linux). Prérequis : Java 17+ et Maven.
set -e
cd "$(dirname "$0")"
mvn -B -q -DskipTests package
JAVA_OPTS=""
# GLFW exige le thread principal sur macOS
[ "$(uname)" = "Darwin" ] && JAVA_OPTS="-XstartOnFirstThread"
exec java $JAVA_OPTS -jar target/ryuukon-palace-1.0-SNAPSHOT.jar
