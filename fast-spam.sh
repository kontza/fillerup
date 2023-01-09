for i in (seq 0 1000)
    http POST http://localhost:9110/ Content-Type:application/octet-stream < src/main/resources/sample-image.jpg
end
