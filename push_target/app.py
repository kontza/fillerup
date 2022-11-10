#!/usr/bin/env python3
import os
import tempfile
from flask import Flask, make_response, request


UPLOAD_FOLDER = "uploads"
ALLOWED_EXTENSIONS = {"txt", "pdf", "png", "jpg", "jpeg", "gif"}

app = Flask(__name__)
app.config.update(
    TESTING=True,
    SECRET_KEY="192b9bdd22ab9ed4d12e236c78afcb9a393ec15f71bbf5dc987d54727823bcbf",
)


@app.route("/", methods=["POST"])
def upload_file():
    if request.headers["Content-Type"] == "application/octet-stream":
        raw_data = request.data
        app.logger.info(f"Got {len(raw_data)} bytes...")
        (fd, filename) = tempfile.mkstemp()
        with os.fdopen(fd, "wb") as tfile:
            tfile.write(raw_data)
        app.logger.info(f"Payload stored to '{filename}'...")
        r = make_response("OK")
        r.mimetype = "text/plain"
        return r
    else:
        return "FAIL"


if __name__ == "__main__":
    app.run(debug=True, host="127.0.0.1", port="7110")
