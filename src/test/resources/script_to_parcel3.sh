cp "/users/username/applications/jars/MyRemoteApp.jar" "archive_0" && \

current_dir=$(pwd) && \
cd "files" && \
zip -o "../archive_0/MyRemoteApp.jar" "com/example/App.class" \
      "com/example/util/Util\$1.class" && \
cd "$current_dir" && \

mv "/users/username/applications/jars/MyRemoteApp.jar" \
  "/users/username/applications/jars/MyRemoteApp.jar-bk" && \
mv "archive_0/MyRemoteApp.jar" "/users/username/applications/jars/MyRemoteApp.jar"
