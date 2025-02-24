#!/usr/bin/env python
"""Despliega tu aplicación web en tu contenedor Docker de la FDI

Requiere:
    ejecutarse desde consola en la misma carpeta en la que esté tu pom.xml, volcado de BD, y ficheros de datos
    que tu proyecto esté actualizado (y en particular, que tenga un application-container.properties)
    credenciales en un fichero credentials.json (NO SUBAS CREDENCIALES A GITHUB)
        puedes ver sus valores orientativos en credentials.json.template
    
Cómo ejecutar:
    1. instala las dependencias, bien vía pip ó con un entorno virtual python
        entorno virtual: créalo vía python3 -m venv deploy
                         actívalo vía domjudge/scripts/activate.ps1 (Windows) ó source deploy/bin/activate (Linux)
                         dependencias vía pip install -r requirements.txt
    2. asegúrate de que tu base de datos está escribiéndose a disco, con JDBC URL jdbc:h2:file:./iwdb
    3. asegúrate de que estás guardando ficheros de usuario a disco, a ./iwdata
    4. ejecuta el script: python deploy.py      
"""

import sys
import subprocess
import glob
import json
import sshtunnel # instala via pip install sshtunnel (ó entorno virtual, ver más arriba)
import fabric    # instala via pip install fabric

print("Checking for database files... ")
dbfiles = glob.glob("iwdb.*")
if len(dbfiles) > 0:
    print(f"Found {len(dbfiles)} database files: {dbfiles}")
else:
    print("No database files found, exiting.")
    sys.exit(1)

print("Checking for data files... ")
datafiles = glob.glob("iwdata", recursive=True)
if len(datafiles) > 0:
    print(f"Found {len(datafiles)} data files, compressing... ")
    subprocess.run(["zip", "-r", "iwdata.zip", "iwdata"], shell=True, check=True)
else:
    print("No data files found.")
    sys.exit(1)

print("Building deployment jar file, using container profile... ")
try:
    subprocess.run(["mvn", 
                    "package", 
                    "-Dspring.profiles.active=container", 
                    "-DskipTests=true"], shell=True, check=True)
    jar = glob.glob("target/*.jar")[0]
    print(f"Deployment jar file is ready: {jar}")
except:
    print("Error: Could not build jar file. Exiting.")

print("Loading credentials from `credentials.json` ... ")
try:
    credentials = json.load(open('credentials.json'))
except:
    print("Error: Could not load credentials file. Exiting.")
    sys.exit(1)

print("Connecting to jumphost ... ")
with sshtunnel.open_tunnel(
    (credentials['jumphost'], 22),
    ssh_username=credentials['jumphost_user'],
    ssh_password=credentials['jumphost_pass'],
    remote_bind_address=(credentials['target'], 22),
    local_bind_address=('0.0.0.0', 2222)
) as tunnel:
    print(f"Tunnel to {credentials['jumphost']} over port 22 established, bind via localhost 2222 ...")
    with fabric.connection.Connection(
        host='127.0.0.1',
        user=credentials['target_user'],
        port=2222,
        connect_kwargs={
            "password": credentials['target_pass']
        }
    ) as c:
        print(f"Connected to target host {credentials['target']} as {credentials['target_user']}")
        print("Uploading database files ... ")
        for f in dbfiles:
            c.put(f)
        print("Uploading data files ... ")
        c.put("iwdata.zip")
        c.run("unzip iwdata.zip && rm iwdata.zip")
        print("Uploading jar file ... ")            
        c.put(jar)
        print(f"All files uploaded, deploy via `SPRING_PROFILES_ACTIVE=container java -jar {jar}` FROM TMUX to keep it running.")

