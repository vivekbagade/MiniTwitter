package RabbitMQ.model;

import org.codehaus.jackson.annotate.JsonIgnore;


public class User {

    @JsonIgnore
    public String password;
    public String name;
    public String email;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
