rmdir /s /q bin
mkdir bin
javac -d bin -sourcepath src src\module-info.java src\dataStructure\*.java src\ui\*.java src\util\*.java
java --module-path bin --module cookierunTCG/ui.MainUI
pause