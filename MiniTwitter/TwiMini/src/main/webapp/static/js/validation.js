
/**
 * Created with IntelliJ IDEA.
 * User: vikramgoyal
 * Date: 7/29/13
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */

var nameValidation = function(username) {
    console.log("Username entered" + username);
    var specialCharacters = /[$&+,:;=?@#|]/;
    if(username=="") {
        console.log("Username cannot be empty");
        return false;
    }
    else if(username.match(specialCharacters)) {
        console.log("Username contains an invalid character");
        return false;

    }
    return true;

}

var emailValidation = function(email) {
    console.log("Email entered" + email);
    var specialCharacters = /[$&+,:;=?#|]/;

    if(email.match(specialCharacters)) {
        console.log("Email id contains an invalid character");
        return false;
    }
    else if(email.match(/@/)==null) {
        console.log("This is not a valid Email id");
        return false;
    }

    return true;
}

var passwordvalidation = function(password) {
    console.log("Password entered"+ password);

    if(password=="") {
        console.log("Password cannot be empty");
        return false;
    }
    return true;
}

