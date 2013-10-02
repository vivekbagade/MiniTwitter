/**
 * Created with IntelliJ IDEA.
 * User: vikramgoyal
 * Date: 7/31/13
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
function followuser(otherEmail,elemId) {

    console.log(otherEmail);

    var email = $.cookie('email');
    var encodedemail = $.base64('encode',email);
    var JsonP= {"email":email,"otherEmail":otherEmail,"token": $.cookie('token')}
    console.log(JSON.stringify(JsonP));
    var url= "/users/" + encodedemail;
    console.log(url);
    $.ajax({
        url: url,
        type: "PUT",
        data: JSON.stringify(JsonP),
        contentType: 'application/json',
        success: function(data) {
            //console.log(data);
            console.log("User successfully followed");
            $('#' +elemId).val("Following");
            assignMouseEntertoFollow(elemId);
            assignMouseLeavetoFollow(elemId);
       },
        error:function(data,status,er) {
            console.log("error: "+data+" status: "+status+" er:"+er);
            console.log("error: "+data+" status: "+status+" er:"+er);
        }
    })
}

function unfollowuser(otherEmail,elemId) {

    console.log("Enter the unfollow user function\n");
    console.log(otherEmail);

    var email = $.cookie('email');
    var encodedemail = $.base64('encode',email);
    var JsonP= {"email":email,"otherEmail":otherEmail,"token": $.cookie('token')}
    console.log(JSON.stringify(JsonP));
    var url= "/subscriptions/" + encodedemail;
    console.log(url);
    $.ajax({
        url: url,
        type: "DELETE",
        data: JSON.stringify(JsonP),
        contentType: 'application/json',
        success: function(data) {
            //console.log(data);
            console.log("User successfully unfollowed");
            $('#' +elemId).val("Follow");
            $('#' + elemId).unbind('mouseenter');
            $('#' + elemId).unbind('mouseleave');

        },
        error:function(data,status,er) {
            console.log("error: "+data+" status: "+status+" er:"+er);
            console.log("error: "+data+" status: "+status+" er:"+er);
        }
    })

}
