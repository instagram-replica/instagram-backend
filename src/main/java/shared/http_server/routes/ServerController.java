package shared.http_server.routes;

import org.json.JSONObject;
import shared.Settings;
import shared.http_server.Server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownServiceException;
import java.util.Properties;

import static shared.Helpers.createJSONError;
import static utilities.Main.readPropertiesFile;

public class ServerController {
    public static JSONObject shutdown(JSONObject params) {
        try {
            Server.close();
            System.out.println("Server was shutdown by external signal!");
            return new JSONObject().put("success", true);
        } catch (IOException e) {
            return new JSONObject().put("error", e.getMessage());
        }
    }

    public static JSONObject updateMethod(JSONObject params) {

        try {
            String service = params.getString("service");
            String methodName = params.getString("methodName");
            Properties routsProps = null;
            routsProps = readPropertiesFile("src/main/resources/requests_mapping.properties");
            if (routsProps.containsKey(methodName))
                return addMethod(params).put("status", "updated");
            else
                return new JSONObject().put("status", "method name not found to be updated");
        } catch (IOException e) {
            e.printStackTrace();
            return createJSONError("Internal Server Error");
        }
    }

    public static JSONObject deleteMethod(JSONObject params) {
        try {
            String service = params.getString("service");
            String methodName = params.getString("methodName");
            String methodClassName = params.getString("methodClassName");
            Properties routsProps = readPropertiesFile("src/main/resources/requests_mapping.properties");
            boolean deletedFromRouter = routsProps.remove(methodName, service);
            FileOutputStream output = new FileOutputStream("src/main/resources/requests_mapping.properties");
            routsProps.store(output, null);

            boolean removedFromService = false;

            switch (service) {
                case "users":
                    Properties userProps = readPropertiesFile("src/main/resources/users_mapper.properties");
                    removedFromService = userProps.remove(methodName, methodClassName);
                    FileOutputStream users_output = new FileOutputStream("src/main/resources/users_mapper.properties");
                    userProps.store(users_output, null);
                    services.users.Controller.props = userProps;
                    break;
                case "posts":
                    Properties postProps = readPropertiesFile("src/main/resources/posts_mapper.properties");
                    removedFromService = postProps.remove(methodName, methodClassName);
                    FileOutputStream posts_output = new FileOutputStream("src/main/resources/posts_mapper.properties");
                    postProps.store(posts_output, null);
                    services.posts.Controller.props = postProps;
                    break;
                case "stories":
                    Properties storyProps = readPropertiesFile("src/main/resources/stories_mapper.properties");
                    removedFromService = storyProps.remove(methodName, methodClassName);
                    FileOutputStream stories_output = new FileOutputStream("src/main/resources/stories_mapper.properties");
                    storyProps.store(stories_output, null);
                    services.stories.Controller.props = storyProps;
                    break;
                case "activities":
                    Properties activityProps = readPropertiesFile("src/main/resources/activities_mapper.properties");
                    removedFromService = activityProps.remove(methodName, methodClassName);
                    FileOutputStream activities_output = new FileOutputStream("src/main/resources/activities_mapper.properties");
                    activityProps.store(activities_output, null);
                    services.activities.Controller.props = activityProps;
                    break;
                case "chats":
                    Properties chatsProps = readPropertiesFile("src/main/resources/chats_mapper.properties");
                    removedFromService = chatsProps.remove(methodName, methodClassName);
                    FileOutputStream chats_output = new FileOutputStream("src/main/resources/chats_mapper.properties");
                    chatsProps.store(chats_output, null);
                    services.chats.Controller.props = chatsProps;
                    break;
                default:
                    return new JSONObject().put("status", "service not found");
            }
            if (deletedFromRouter && removedFromService) {
                return new JSONObject().put("status", "method deleted successfully");
            } else {
                return new JSONObject().put("status", "method not deleted successfully");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return createJSONError("Internal Server Error");
        }
    }




    public static JSONObject addMethod(JSONObject params) {
        JSONObject response = new JSONObject();
        try {
            String service = params.getString("service");
            String methodName = params.getString("methodName");
            String methodClassName = params.getString("methodClassName");

            // add method to requests mapping
            Properties routsProps = readPropertiesFile("src/main/resources/requests_mapping.properties");

            if (routsProps.containsKey(methodName))
                return new JSONObject().put("status", "method already exists");

            routsProps.setProperty(methodName, service);
            FileOutputStream output = new FileOutputStream("src/main/resources/requests_mapping.properties");
            routsProps.store(output, null);

            // add method to its service mapper
            switch (service) {
                case "users":
                    Properties userProps = readPropertiesFile("src/main/resources/users_mapper.properties");
                    userProps.setProperty(methodName, methodClassName);
                    FileOutputStream users_output = new FileOutputStream("src/main/resources/users_mapper.properties");
                    userProps.store(users_output, null);
                    services.users.Controller.props = userProps;
                    break;
                case "posts":
                    Properties postProps = readPropertiesFile("src/main/resources/posts_mapper.properties");
                    postProps.setProperty(methodName, methodClassName);
                    FileOutputStream posts_output = new FileOutputStream("src/main/resources/posts_mapper.properties");
                    postProps.store(posts_output, null);
                    services.posts.Controller.props = postProps;
                    break;
                case "stories":
                    Properties storyProps = readPropertiesFile("src/main/resources/stories_mapper.properties");
                    storyProps.setProperty(methodName, methodClassName);
                    FileOutputStream stories_output = new FileOutputStream("src/main/resources/stories_mapper.properties");
                    storyProps.store(stories_output, null);
                    services.stories.Controller.props = storyProps;
                    break;
                case "activities":
                    Properties activityProps = readPropertiesFile("src/main/resources/activities_mapper.properties");
                    activityProps.setProperty(methodName, methodClassName);
                    FileOutputStream activities_output = new FileOutputStream("src/main/resources/activities_mapper.properties");
                    activityProps.store(activities_output, null);
                    services.activities.Controller.props = activityProps;
                    break;
                case "chats":
                    Properties chatsProps = readPropertiesFile("src/main/resources/chats_mapper.properties");
                    chatsProps.setProperty(methodName, methodClassName);
                    FileOutputStream chats_output = new FileOutputStream("src/main/resources/chats_mapper.properties");
                    chatsProps.store(chats_output, null);
                    services.chats.Controller.props = chatsProps;
                    break;
                default:
                    return new JSONObject().put("status", "service not found");
            }
        } catch (IOException e) {
            return new JSONObject().put("status", "adding method failed");
        }

        //add props of service
        return new JSONObject().put("status", "success");
    }

    public static JSONObject getInfo(JSONObject params) {
        return Settings.getInstance().toJSON();
    }

}
