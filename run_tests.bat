@echo off
echo ===== Execution des tests unitaires pour Ryuukon Palace =====

rem Définir les chemins
set CLASSPATH=.;target\classes;target\test-classes
set CLASSPATH=%CLASSPATH%;lib\*
set CLASSPATH=%CLASSPATH%;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar;lib\mockito-core-5.3.1.jar;lib\byte-buddy-1.14.4.jar;lib\objenesis-3.3.jar

rem Compiler les classes de test
echo Compilation des classes de test...
javac -d target\test-classes -cp %CLASSPATH% src\test\java\com\ryuukonpalace\game\TestRunner.java src\test\java\com\ryuukonpalace\game\core\*.java src\test\java\com\ryuukonpalace\game\player\*.java src\test\java\com\ryuukonpalace\game\mystical\*.java src\test\java\com\ryuukonpalace\game\ui\*.java src\test\java\com\ryuukonpalace\game\audio\*.java

rem Exécuter les tests
echo Execution des tests...
java -cp %CLASSPATH% com.ryuukonpalace.game.TestRunner

echo ===== Fin des tests =====
pause
