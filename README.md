# Codegenerator Docker Anleitung

## Schritte zum Ausführen

1. **In das Verzeichnis navigieren:**
   
   Öffne ein Terminal im Docker-Container und wechsle in folgendes Verzeichnis:
     ```
   cd ib610/codegenerator
     ```


3. **Code generieren:**

Führe den Befehl aus:
  ```
make
  ```

3. **Tests erstellen:**

Danach starte den Testlauf:
  ```
make test
  ```


4. **Testausgabe:**

Wenn der Test erfolgreich war, wird eine Datei namens `test.out` im aktuellen Verzeichnis erstellt.

5. **MIPS-Simulation:**

2 Möglichkeiten, die generierte Datei auszuführen:

- **Außerhalb des Docker-Containers:**  
  Öffne einen MIPS-Simulator wie **MARS** und kopiere den Inhalt der `test.out` Datei hinein, um das Programm zu starten.

- **Innerhalb des Docker-Containers:**  
  Nutze den integrierten SPIM-Simulator, indem du folgenden Befehl eingibst:
  ```
  spim -file test.out
  ```

---

## Hinweise
- im Verzeichnis befindet sich ein Makefile für weitere Infos
