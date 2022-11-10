# TL;DR
1. Prepare Flask-app's virtual environment: `./push_target/venv-prep.sh`.
2. Start the Flask-app: `./push_target/app.py`.
3. Start this Java app: `./run-spring-boot.sh`.
4. Run

   ```bash
   # Fish
   $ for i in (seq 0 10) ; http http://localhost:6110/trigger;end
   # Bash/Fish
   $ for i in $(seq 0 10);do http http://localhost:6110/trigger;done
   ```
