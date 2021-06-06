cp "/users/username/applications/jars/MyRemoteApp.jar" "archive_0" && \

current_dir=$(pwd) && \
cd "archive_1" && \
unzip "../archive_0/MyRemoteApp.jar" "nested/folder/Inner.zip" && \
cd "$current_dir" && \

current_dir=$(pwd) && \
cd "archive_2" && \
unzip "../archive_1/nested/folder/Inner.zip" "more/nested/folder/InnerMost.zip" && \
cd "$current_dir" && \

current_dir=$(pwd) && \
cd "files" && \
zip -o "../archive_2/more/nested/folder/InnerMost.zip" "com/example/App.class" \
      "com/example/util/Util\$1.class" && \
cd "$current_dir" && \

current_dir=$(pwd) && \
cd "archive_2" && \
zip -o "../archive_1/nested/folder/Inner.zip" "more/nested/folder/InnerMost.zip" && \
cd "$current_dir" && \

current_dir=$(pwd) && \
cd "archive_1" && \
zip -o "../archive_0/MyRemoteApp.jar" "nested/folder/Inner.zip" && \
cd "$current_dir" && \

mv "/users/username/applications/jars/MyRemoteApp.jar" \
  "/users/username/applications/jars/MyRemoteApp.jar-bk" && \
mv "archive_0/MyRemoteApp.jar" "/users/username/applications/jars/MyRemoteApp.jar"