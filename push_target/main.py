from fastapi import FastAPI, Request, HTTPException
import random
import tempfile

app = FastAPI()

UPLOAD_FOLDER = "uploads"
CHUNK_SIZE = 655360


@app.post("/")
async def post_endpoint(request: Request):
    if random.random() > 0.5:
        # 10% change of failing the request due to a non-existent directory.
        target = tempfile.NamedTemporaryFile(dir=f"{UPLOAD_FOLDER}s")
    else:
        target = tempfile.NamedTemporaryFile(dir=UPLOAD_FOLDER, delete=False)
    count = 0
    async for chunk in request.stream():
        target.write(chunk)
        count = count + len(chunk)
    return {"result": "OK"}
