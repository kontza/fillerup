from fastapi import FastAPI, File, UploadFile, HTTPException
import aiofiles

app = FastAPI()

UPLOAD_FOLDER = "uploads"
CHUNK_SIZE = 655360


@app.post("/")
async def post_endpoint(payload: UploadFile = File(...)):
    out_file_path = f"{UPLOAD_FOLDER}/{payload.filename}"
    if not payload.content_type.startswith("image"):
        raise HTTPException(400, detail="Invalid document type")
    async with aiofiles.open(out_file_path, "wb") as out_file:
        while content := await payload.read(CHUNK_SIZE):
            await out_file.write(content)
    return {"Result": "OK"}
