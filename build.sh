docker rmi instagram-backend_user_server --force
docker rmi instagram-backend_http_server --force
docker rmi instagram-backend_sql_migrations --force
docker rmi instagram-backend_chats_server --force
docker rmi instagram-backend_activities_server --force
docker rmi instagram-backend_posts_server --force
docker rmi instagram-backend_stories_server --force

mvn clean compile package 

