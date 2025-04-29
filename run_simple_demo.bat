@echo off
echo ===== Ryuukon Palace - Demarrage du demonstrateur =====
echo.

:: Vérifier si Java est installé
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERREUR] Java n'est pas installe ou n'est pas dans le PATH.
    echo Veuillez installer Java et reessayer.
    pause
    exit /b 1
)

echo [INFO] Preparation de l'environnement...

:: Créer les répertoires nécessaires s'ils n'existent pas
if not exist "target\classes" mkdir target\classes

echo [INFO] Compilation du projet...

:: Compiler la classe du démonstrateur simplifié
javac -d target/classes src/main/java/com/ryuukonpalace/game/demo/SimpleDemoLauncher.java

:: Vérifier si la compilation a réussi
if %errorlevel% neq 0 (
    echo [ERREUR] Echec de la compilation. Verifiez les messages d'erreur ci-dessus.
    pause
    exit /b %errorlevel%
)

echo.
echo [SUCCES] Compilation terminee avec succes!
echo.
echo [INFO] Execution du demonstrateur...
echo.
echo ===== COMMANDES =====
echo - Fleches ou ZQSD : Deplacer le personnage
echo - ESC : Quitter
echo ===================
echo.

:: Exécuter le démonstrateur
java -cp "target/classes" com.ryuukonpalace.game.demo.SimpleDemoLauncher

echo.
echo ===== Fin du demonstrateur =====
echo Pour plus d'informations, consultez GUIDE_COMPLET.md
pause
