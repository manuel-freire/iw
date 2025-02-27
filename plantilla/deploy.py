#!/usr/bin/env python
"""Despliega tu aplicación web en tu contenedor Docker de la FDI

Requiere:
    ejecutarse desde consola en la misma carpeta en la que esté tu pom.xml, volcado de BD, y ficheros de datos
    que tu proyecto esté actualizado (y en particular, que tenga un application-container.properties)
    credenciales en un fichero credentials.json (NO SUBAS CREDENCIALES A GITHUB)
        puedes ver sus valores orientativos en credentials.json.template
    
Cómo ejecutar:
    1. instala las dependencias, bien vía pip ó con un entorno virtual python
        entorno virtual: créalo vía `python3 -m venv deploy`
                         actívalo vía `deploy/scripts/activate.ps1` (Windows) ó `source deploy/bin/activate` (Linux)
                         instala dependencias vía `pip install -r requirements.txt`
    2. asegúrate de que tu base de datos está escribiéndose a disco, con JDBC URL jdbc:h2:file:./iwdb
    3. asegúrate de que estás guardando ficheros de usuario a disco, a ./iwdata
    4. ejecuta el script: python deploy.py      
"""

# dependencias no-incluídas en Python estándar
import sshtunnel # instala via pip install sshtunnel (ó entorno virtual, ver más arriba)
import fabric    # instala via pip install fabric

# dependencias incluídas en Python estándar
import sys
import subprocess
import glob
import json
import zipfile
from typing import Union
from pathlib import Path
import argparse

# see https://stackoverflow.com/a/68817065/15472
def zip_dir(dir: Union[Path, str], filename: Union[Path, str]):
    """Zip the provided directory without navigating to that directory using `pathlib` module"""

    # Convert to Path object
    dir = Path(dir)

    with zipfile.ZipFile(filename, "w", zipfile.ZIP_DEFLATED) as zip_file:
        for entry in dir.rglob("*"):
            zip_file.write(entry, entry.relative_to(dir))

def main(credentials_file, db_file_root, data_file_root):
  print(f"Checking for database files... (should be named {db_file_root}; ensure you have spring.datasource.url=jdbc:h2:file:./{db_file_root}) ")
  dbfiles = glob.glob(f"{db_file_root}.*")
  if len(dbfiles) > 0:
      print(f"Found {len(dbfiles)} database files: {dbfiles}")
  else:
      print("No database files found, exiting.")
      sys.exit(1)

  print(f"Checking for data files... (should be in {data_file_root}; ensure you have es.ucm.fdi.base-path=./{data_file_root}) ")
  datafiles = glob.glob(data_file_root, recursive=True)
  if len(datafiles) > 0:
      print(f"Found {len(datafiles)} data files, compressing... ")
      zip_dir(data_file_root, "iwdata.zip")
  else:
      print("No data files found.")
      sys.exit(1)

  print("Building deployment jar file... ")
  try:
      subprocess.run(["mvn",
                      "package", 
                      "-DskipTests=true"], shell=False, check=True)
      jar = glob.glob("target/*.jar")[0]
      print(f"Deployment jar file is ready: {jar}")
  except:
      print("Error: Could not build jar file. Exiting.")

  print(f"Loading credentials from `{credentials_file}` ... ")
  try:
      credentials = json.load(open(credentials_file))
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
          c.run(f"unzip iwdata.zip -d {data_file_root} && rm iwdata.zip")
          print("Uploading jar file ... ")            
          c.put(jar)
          print(f"All files uploaded. Killing previous servers ...")
          c.run("tmux kill-server || true")
          print(f"creating a launch script (`run.sh`) for the web application ...")
          c.run(f"echo 'SPRING_PROFILES_ACTIVE=container java -jar $(basename {jar})' > run.sh && chmod +x run.sh")
          print(f"... and deploying in new tmux session `iw`; connect via `tmux a -t iw` to see logs")
          c.run(f"tmux new-session -d -s iw ./run.sh")
          print("Deployment complete, check logs for errors.")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=\
        "Upload all components of a Spring Boot application to a remote server")
    parser.add_argument("--credentials", "-c", type=str, default="credentials.json", help="Path to credentials")
    parser.add_argument("--db_file_root", "-d", type=str, default="iwdb", help="Root name of database files (see application.properties' spring.datasource.url)")
    parser.add_argument("--data_file_root", "-f", type=str, default="iwdata", help="Folder with data files (see application.properties' es.ucm.fdi.base-path)")
    args = parser.parse_args()
    try:
      main(args.credentials, args.db_file_root, args.data_file_root)
    except Exception as e:
      print(f"Aborting due to error: {e}")
      sys.exit(1)
    sys.exit(0)
