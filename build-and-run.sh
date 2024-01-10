export MAIN_CLASS=com.gpl.rpg.atcontentstudio.ATContentStudio
export CP="lib/*:src:res:hacked-libtiled:siphash-zackehh/src/main/java/:."
javac -cp $CP ./src/com/gpl/rpg/atcontentstudio/ATContentStudio.java
java -cp $CP $MAIN_CLASS
