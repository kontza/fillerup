for i in (seq 0 1000)
    ➤http -f POST http://localhost:9110/ payload@src/main/resources/sample-image.jpg
end
