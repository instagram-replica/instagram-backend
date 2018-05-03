docker rmi instagram-backend_user_server --force
docker rmi instagram-backend_http_server --force
docker rmi instagram-backend_sql_migrations --force

mvn clean compile package 

