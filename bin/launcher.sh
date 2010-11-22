PROJ_DIR=$(dirname $0)/..
CLASSPATH=$PROJ_DIR/target/minotaur-1.0-SNAPSHOT-jar-with-dependencies.jar 
java -Xdebug \
    -cp "$CLASSPATH" \
    org.wol.minotaur.main \
    "$@"
