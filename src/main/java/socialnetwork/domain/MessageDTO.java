package socialnetwork.domain;

import java.util.Objects;

public class MessageDTO {

    private String name;
    private int numberOfMessages;


    public MessageDTO(String name, int numberOfMessages) {
        this.name=name;
        this.numberOfMessages = numberOfMessages;
    }


    public String getName(){
        return name;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }


    @Override
    public String toString() {
        return "MessageDTO{" +
                "name='" + name + '\'' +
                ", numberOfMessages=" + numberOfMessages +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageDTO that = (MessageDTO) o;
        return numberOfMessages == that.numberOfMessages &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, numberOfMessages);
    }
}
