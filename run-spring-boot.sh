tmux new -d 'mvn spring-boot:run -Dspring-boot.run.jvmArguments="-XX:MaxDirectMemorySize=6m -Xms64m -Xmx64m -XX:+CrashOnOutOfMemoryError"'
