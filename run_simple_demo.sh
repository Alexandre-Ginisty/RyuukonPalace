#!/usr/bin/env bash
# Lance la démo simplifiée (Swing, sans dépendances). Prérequis : Java 17+.
set -e
cd "$(dirname "$0")"
mkdir -p target/classes
javac -d target/classes src/main/java/com/ryuukonpalace/game/demo/SimpleDemoLauncher.java
exec java -cp target/classes com.ryuukonpalace.game.demo.SimpleDemoLauncher
