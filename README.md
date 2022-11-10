# TL;DR
1. Run `./push_target/venv-prep.sh`.
2. Run `./push_target/app.py`.
3. Start this Java app with `-XX:MaxDirectMemorySize=2m`.
4. Run

   ```bash
   # Fish
   $ for i in (seq 0 10) ; http http://localhost:6110/trigger &;end
   # Bash/Fish
   $ for i in $(seq 0 10);do http http://localhost:6110 &;done
   ```
