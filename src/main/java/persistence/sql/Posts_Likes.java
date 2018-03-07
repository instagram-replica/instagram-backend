package persistence.sql;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import static utilities.Main.generateUUID;

@Table("posts_likes")
public class Posts_Likes extends Model{
    public void setId(){
        set("id", generateUUID());
    }
    public  void setUserId(String userId){
        set("user_id", userId);
    }
    public  String getCreatedAt(){
        return getString("created_at");
    }
    public  void setPostId(String postId){
        set("post_id", postId);
    }
}
