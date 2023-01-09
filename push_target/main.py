from fastapi import FastAPI, Request
import tempfile

app = FastAPI()

UPLOAD_FOLDER = "uploads"
CHUNK_SIZE = 655360


@app.post("/")
async def post_endpoint(request: Request):
    target = tempfile.NamedTemporaryFile(dir=UPLOAD_FOLDER)
    count = 0
    async for chunk in request.stream():
        target.write(chunk)
        count = count + len(chunk)
    return {"result": "OK"}
