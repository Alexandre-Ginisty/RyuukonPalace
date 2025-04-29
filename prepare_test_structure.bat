@echo off
echo ===== Preparation de la structure pour les tests =====

rem Créer les répertoires nécessaires
if not exist target mkdir target
if not exist target\classes mkdir target\classes
if not exist target\test-classes mkdir target\test-classes

rem Copier les classes compilées du projet vers target\classes
echo Copie des classes compilées...
xcopy /E /I /Y "src\main\java\*.class" "target\classes\" 2>nul
if errorlevel 1 echo Aucune classe compilée trouvée, cela peut causer des erreurs lors des tests

echo ===== Structure préparée =====
echo Exécutez d'abord download_dependencies.bat pour télécharger les dépendances
echo Puis exécutez run_tests.bat pour lancer les tests
pause
