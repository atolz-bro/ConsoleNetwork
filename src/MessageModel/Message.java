package MessageModel;

import java.io.Serializable;

public class Message implements Serializable {
    public String message;
    public String fromName;
    public String toName;

    public Message(String message, String fromName, String toName) {
        this.message = message;
        this.fromName = fromName;
        this.toName = toName;
    }

    public String getMessage() {
        return message;
    }

    public String getFromName() {
        return fromName;
    }

    public String getToName() {
        return toName;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", fromName='" + fromName + '\'' +
                ", toName='" + toName + '\'' +
                '}';
    }
}
