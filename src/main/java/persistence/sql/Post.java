package persistence.sql;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("posts")
public class Post extends Model {
    public void setCaption(String caption){
        set("caption", caption);
    }
    public String getCreatedAt(){
        return getString("created_at");
    }
    public String getCaption(){
        return getString("caption");
    }

}
