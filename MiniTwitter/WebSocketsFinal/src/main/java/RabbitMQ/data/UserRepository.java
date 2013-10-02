package RabbitMQ.data;


import RabbitMQ.model.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private static JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
          this.jdbcTemplate=jdbcTemplate;
    }

    public List<User> getFollowers(String email,String lastfolloweremail)
    {
        if(lastfolloweremail!="000")
            return jdbcTemplate.query("SELECT email,name from users where email in (select uemail from following where femail=?  AND email > ?) ORDER BY email LIMIT 20",new Object[]{email,lastfolloweremail},new BeanPropertyRowMapper<User>(User.class));
        return jdbcTemplate.query("SELECT email,name from users where email in (select uemail from following where femail=? ) ORDER BY email LIMIT 20",new Object[]{email},new BeanPropertyRowMapper<User>(User.class));
    }

    public List<User> getSubscriptions(String email,String lastSubscriptionEmail)
    {
        if(lastSubscriptionEmail.equals("000"))
            return jdbcTemplate.query("SELECT email,name from users where email in (select femail from following where uemail=?) ORDER BY email LIMIT 10",new Object[]{email},new BeanPropertyRowMapper<User>(User.class));
        return jdbcTemplate.query("SELECT email,name from users where email in (select femail from following where uemail=? AND femail> ?) ORDER BY email LIMIT 10",new Object[]{email,lastSubscriptionEmail},new BeanPropertyRowMapper<User>(User.class));
    }

    public void addUser(String name, String password, String emailID) {
        System.out.println("User inserted "+name+" "+password+" "+emailID);
        jdbcTemplate.execute("INSERT INTO users(name,password,email) values ('" + name + "', '" + password + "', '" + emailID + "')");
    }

    public void modifyUser(String emailId,String name,String password) {
        jdbcTemplate.update("UPDATE users set password=?,name=? where email=?", new Object[]{password,name, emailId});
    }

    public List<User> findUserbyEmail(String email)
    {
        return jdbcTemplate.query("SELECT email,name,password from users where email=?", new Object[]{email}, new BeanPropertyRowMapper<User>(User.class));
    }

    public boolean isUserPresent(String email) {
        try{
            List<User> userList = jdbcTemplate.query("select name from users where email=?",
                    new Object[]{email}, new BeanPropertyRowMapper<User>());
            if(userList.size() > 0){
                return true;
            }
        }
        catch (Exception e){
            return true;
        }
        return false;
    }

    /*public int getUserIDByEmail(String email)
    {
        return jdbcTemplate.queryForObject("Select userid from users where email=?",new Object[]{email},new BeanPropertyRowMapper<User>(User.class)).getUserID();
    }*/

    public void unfollow(String one,String two)
    {
        jdbcTemplate.execute("delete from following where uemail='"+one+"' and femail='"+two+"'");
    }

    public void follow(String one,String two)
    {
        jdbcTemplate.execute("insert into following values('"+one+"','"+two+"')");
    }

}

