@echo off
echo ===== Demarrage du demonstrateur Ryuukon Palace =====
echo Preparation de l'environnement...

:: Créer les répertoires nécessaires s'ils n'existent pas
if not exist "target\classes" mkdir target\classes
if not exist "lib" mkdir lib

:: Nous ne téléchargeons plus les dépendances car cela pose des problèmes de connexion
echo Les dependances LWJGL doivent etre placees manuellement dans le dossier "lib"
echo Si les fichiers JAR ne sont pas presents, la compilation pourrait echouer

echo Compilation du projet...

:: Compiler les classes principales
javac -d target/classes -cp "lib/*" src/main/java/com/ryuukonpalace/game/core/*.java src/main/java/com/ryuukonpalace/game/utils/*.java src/main/java/com/ryuukonpalace/game/world/*.java 2>nul

:: Vérifier si la compilation a réussi
if %errorlevel% neq 0 (
    echo Erreur de compilation des classes principales. Tentative de compilation avec options simplifiees...
    javac -d target/classes src/main/java/com/ryuukonpalace/game/core/*.java src/main/java/com/ryuukonpalace/game/utils/*.java src/main/java/com/ryuukonpalace/game/world/*.java 2>nul
)

:: Compiler les classes de démo
javac -d target/classes -cp "target/classes;lib/*" src/main/java/com/ryuukonpalace/game/demo/*.java 2>nul

:: Vérifier si la compilation a réussi
if %errorlevel% neq 0 (
    echo Erreur de compilation. Veuillez verifier les messages d'erreur ci-dessus.
    echo Compilation avec plus de details...
    javac -d target/classes -cp "target/classes;lib/*" -verbose src/main/java/com/ryuukonpalace/game/demo/*.java
    pause
    exit /b %errorlevel%
)

echo.
echo Compilation terminee avec succes!
echo.
echo Execution du demonstrateur...
echo.
echo Utilisez les fleches ou WASD pour deplacer le personnage.
echo Appuyez sur ESC pour quitter.
echo.

:: Exécuter le démonstrateur
java -cp "target/classes;lib/*" com.ryuukonpalace.game.demo.DemoLauncher

echo.
echo ===== Fin du demonstrateur =====
pause
