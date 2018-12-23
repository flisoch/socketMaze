package protocol;

import lombok.Data;

@Data
public class Protocol {
    public enum Command{CREATE_SERVER, SAVE_SERVER_CONFIGURATION, DISCONNECT_MAIN};
}
