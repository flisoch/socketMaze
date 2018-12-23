package Menu;

import java.util.ArrayList;
import java.util.List;

public class ServerList {
    private static List<Server> servers;

    public static List<Server> getServers(){
        if(servers == null){
            servers = new ArrayList<>();
        }
        return servers;
    }

}
