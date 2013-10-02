/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 8/4/13
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */
function logoutUser(email,token)
{
    var url = "/logout?email=" + email+ "&token=" +token;
    console.log(url);
    $.ajax({
        url: url,
        type: "GET",
        success: function(data) {
            var url = "/";
            window.location.href = url;
        }
    })
}