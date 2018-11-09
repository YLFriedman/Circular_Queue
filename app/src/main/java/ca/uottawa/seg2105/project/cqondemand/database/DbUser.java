package ca.uottawa.seg2105.project.cqondemand.database;

import ca.uottawa.seg2105.project.cqondemand.domain.User;

public class DbUser extends DbItem<User> {

    public String first_name;
    public String last_name;
    public String username;
    public String email;
    public String password;
    public String type;

    public DbUser() {}

    public DbUser(User user) {
        first_name = user.getFirstName();
        last_name = user.getLastName();
        username = user.getUserName();
        email = user.getEmail();
        password = user.getPassword();
        type = user.getType().toString();
    }

    public User toItem() { return new User(first_name, last_name, username, email, User.parseType(type), password); }

    public String generateKey() { return DbUtil.getSanitizedKey(username); }

}
