python3 -mvenv venv
echo $SHELL|grep -q 'fish$' && source venv/bin/activate.fish ||source venv/bin/activate
pip install -r requirements.txt
