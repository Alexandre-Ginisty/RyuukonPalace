@echo off
echo ===== Telechargement des dependances pour les tests =====

rem Créer le répertoire lib s'il n'existe pas
if not exist lib mkdir lib

echo Telechargement de JUnit 4.13.2...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar' -OutFile 'lib\junit-4.13.2.jar'"

echo Telechargement de Hamcrest 1.3...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar' -OutFile 'lib\hamcrest-core-1.3.jar'"

echo Telechargement de Mockito 5.3.1...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/mockito/mockito-core/5.3.1/mockito-core-5.3.1.jar' -OutFile 'lib\mockito-core-5.3.1.jar'"

echo Telechargement de Byte Buddy 1.14.4...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy/1.14.4/byte-buddy-1.14.4.jar' -OutFile 'lib\byte-buddy-1.14.4.jar'"

echo Telechargement de Byte Buddy Agent 1.14.4...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/net/bytebuddy/byte-buddy-agent/1.14.4/byte-buddy-agent-1.14.4.jar' -OutFile 'lib\byte-buddy-agent-1.14.4.jar'"

echo Telechargement de Objenesis 3.3...
powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/objenesis/objenesis/3.3/objenesis-3.3.jar' -OutFile 'lib\objenesis-3.3.jar'"

echo ===== Telechargement des dependances termine =====
echo Vous pouvez maintenant executer run_tests.bat pour lancer les tests
pause
