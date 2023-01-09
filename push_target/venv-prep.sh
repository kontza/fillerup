python3 -mvenv venv
echo $SHELL|grep -q 'fish$' && source venv/bin/activate.fish ||source venv/bin/activate
pip install -r push_target/requirements.txt
