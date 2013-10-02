/**
 * Created with IntelliJ IDEA.
 * User: vikramgoyal
 * Date: 7/31/13
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */

function showwhoIAmFollowing(email) {
    var bas64email = $.base64('encode', email);
    var url = '/subscriptions/' + bas64email;
    console.log(url);
    $.ajax({
        async: false,
        url: url,
        type: "GET",
        dataType: 'json',
        success: function(data) {
            console.log(data.length);
            var content="";
            for(var i=0;i<data.length;i++) {

                var followingid = "followingid_" + i;

                var buttontext="";
                if($.cookie('email')===email) {
                    buttontext = "<span class='pull-right'><input class='btn btn-primary' type='submit' value='Following' id =" + followingid +" /></span> ";
                }

                content += '<tr><td><b>' + data[i].email + '</b>' +   buttontext + '</td></tr>';
            }
                if(content.length>0) {
                $('#contentTable tbody').html(content);
                $('#contentTitle').html("Following");
            }
            else {
                content = '<tr><td><b>Currently not following anyone<tr></td></b>';
                $('#contentTable tbody').html(content);
                $('#contentTitle').html("Following");

            }
            for(var i=0;i<data.length;i++) {

                var followingid = "followingid_" + i;
                var id = +(followingid.split("_")[1]) + 1;
                console.log("Row number clicked" + id);
                var otherEmail = $.trim($('#contentTable tr:nth-child(' + id + ')').find("td:first").text());
                assignClicktoFollow(followingid,otherEmail);
                assignMouseEntertoFollow(followingid);
                assignMouseLeavetoFollow(followingid);
            }

            $('#contentTable').find('#getmorerows').remove();

            console.log("We got" + data.length + "rows of data");
            if(data.length==30) {
                console.log("Appending the get more rows feature to the table");
                var timestamp_row = "<tr style='display: none' id='getmorerows'><td>" +  data[29].email + "</td></tr>";
                $('#contentTable').append(timestamp_row);
            }


        },
        error:function(data,status,er) {
            console.log("error: "+data+" status: "+status+" er:"+er);
            console.log("error: "+data+" status: "+status+" er:"+er);
        }
    })


}
function assignClicktoFollow(elemId,otherEmail){
    $('#'+elemId).click(function() {
        if($('#' + elemId).val()=="Unfollow") {
            unfollowuser(otherEmail,elemId);
        }
        else {
            followuser(otherEmail,elemId);
        }
    })
}

function assignMouseEntertoFollow(elemId) {
    $('#' + elemId).mouseenter(function() {

        $('#' + elemId).val("Unfollow");

    })
}

 function assignMouseLeavetoFollow(elemId) {
     $('#' + elemId).mouseleave(function() {

         $('#' + elemId).val("Following");
     })
 }
