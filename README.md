COMPILACION:
    javac -cp "lib/*" -d bin $(find src/main src/test -name "*.java")


COMPILAR Y EJECUTAR TESTS:
    javac -cp "lib/*" -d bin $(find src/main src/test -name "*.java") && java -cp "bin:lib/*" org.junit.runner.JUnitCore test.AllTests


GENERAR ZIP PROYECTO:
    zip -r prop-project.zip . -x ".git/*" "bin/*" "out/*" "build/*" "target/*" "*.class" "prop-project*.zip"


GENERAR DOCUMENTACION JAVADOC:
    javadoc -encoding UTF-8 -charset UTF-8 -d docs/javadoc -sourcepath src/main -subpackages controllers:domain