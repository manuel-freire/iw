#!/bin/bash

#
# Launches and stops standalone HSQLDB servers from command-line. 
# Requires working maven
#

JAVA_OPTS=-Xmx128M
VER=2.5.0
JAR=~/.m2/repository/org/hsqldb/hsqldb/$VER/hsqldb-$VER.jar

function getJar() {
    if ! [ -f $JAR ] ; then
        # from http://stackoverflow.com/a/1896110/15472
        mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get \
            -DrepoUrl=http://repo.maven.apache.org/maven2/ \
            -Dartifact=org.hsqldb:hsqldb:$VER
    fi
}

function start() {
     getJar
     (java $JAVA_OPTS -cp $JAR org.hsqldb.server.Server &)
}

function gui() {
     getJar
     (java -cp $JAR org.hsqldb.util.DatabaseManagerSwing &)
}

function stop() {
    PID=$(ps -ef | grep server.Server | grep -v grep | awk '{print $2}')
    if ! [ -z "$PID" ] ; then kill $PID ; fi
}

function other() {
    getJar
    shift
    echo "java $JAVA_OPTS -cp $JAR org.hsqldb.server.Server $@"
     (java $JAVA_OPTS -cp $JAR org.hsqldb.server.Server $@ &)
}

# help contents
function help() {
cat << EOF
  Uso $0 [OPERACION | --help]
  
  Script de IW para iniciar, examinar y parar servidores de bd
  HSQLDB.

  OPERACION debe ser una de las siguientes:
    
    start:  Arranca el servidor
    gui:    Muestra una interfaz grafica para ver
            tablas y realizar consultas
    stop:   Para el servidor    
  --help    Muestra esta ayuda
EOF
}

# main entrypoint, called after defining all functions
function main() {

    if [[ $# -eq 0 ]] ; then
        echo "  Uso: $0 [OPERACION | --help]"
        exit 0
    fi
    
    case "$1" in
        "start") start ;;
        "stop")  stop ;;
        "gui")   gui ;;
        "other") other $@ ;; 
        "--help") help ;;
        *) echo \
            "  Uso: $0 [OPERACION | --help]" \
            && echo "   ('$1' NO es una operacion valida)'" ;;        
    esac
}

main $@
