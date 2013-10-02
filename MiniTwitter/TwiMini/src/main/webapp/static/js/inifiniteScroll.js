/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 8/7/13
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
function infiniteScroll(userEmail)
{
    var gettingdata = false;
    if (!gettingdata && Math.abs($(window).scrollTop() + $(window).height() - $(document).height())<10 && $('#contentTable').find('#getmorerows').length>0){
        gettingdata=true;
        console.log("Getting data from server\n");
        console.log("Before getting data" + $(window).height() + $(window).scrollTop() + '\n' +$(document).height())

        var timestamp = $ ('#contentTable tr:last').find("td:first").text();
        console.log(timestamp);
        var tablelength  = document.getElementById('contentTable').rows.length;
        console.log("Table Length is" + tablelength);
        tablelength =  tablelength - 2;  //since table header row is also included in the length
        console.log("Modified Table Length is" + tablelength);
        console.log("Following id of newly inserted rows starts from "+ tablelength);

        var email =  $.base64('encode', userEmail);
        console.log("Encoded Email of user" + email);
        var action = $('#contentTitle').text();
        console.log("Action is "+ action);
        var url,followingid;
        if(action== "User Tweets") {
            url = '/tweets/' + email + '/' + timestamp;
        }
        else if(action=="Followers") {
            console.log("Email id of last follower:" + timestamp);
            var lastEmail = $.base64('encode', timestamp);
            console.log("Last Follower Email" +lastEmail);
            url = '/followers/' + email + '/' + lastEmail;
        }
        else if(action=="Following") {
            console.log("Email id of last following:" + timestamp);
            var lastEmail = $.base64('encode', timestamp);

            console.log("Last Following Email" + lastEmail);
            url = '/subscriptions/' + email + '/' + lastEmail;

        }
        else if(action=="Tweets") {
            url = '/feed/'+ email +"/" + timestamp ;
        }
        console.log(url);
        console.log(url);
        $('#contentTable').find('#getmorerows').remove();

        $.ajax({
            url: url,
            type: "GET",
            dataType: 'json',
            mimeType: 'application/json',
            success: function(data) {
                //console.log(data);
                console.log(data.length);
                var content="";
                var timedifference="";
                for(var i=0;i<data.length;i++) {

                    if(action=="User Tweets") {
                        content = '<tr><td><b>' + data[i].email + '</b><span class="pull-right"><b>' + giveTimeDifference(new Date(data[i].timestamp),new Date()) + '</b></span><br>' + data[i].content + '</td></tr>';

                    }
                    else if(action=="Following") {

                        followingid = "followingid_" + (tablelength + i);

                        var buttontext="";
                        if($.cookie('email')===email) {
                            buttontext = "<span class='pull-right'><input class='btn btn-primary' type='submit' value='Following' id =" + followingid +" /></span> ";
                        }

                        content = '<tr><td><b>' + data[i].email + '</b>' +   buttontext + '</td></tr>';

                    }
                    else if(action=="Followers") {
                        content = '<tr><td><b>' + data[i].email + '</b><br></td></tr>';

                    }
                    else if(action=="Tweets") {

                        content = '<tr><td><b>' + data[i].email + '</b><span class="pull-right"><b>' + giveTimeDifference(new Date(data[i].timestamp),new Date()) + '</b></span><br>' + data[i].content + '</td></tr>';

                    }
                    console.log(content);
                    //console.log("\nThat was all the content received by the ajax call\n");
                    $('#contentTable').append(content);
                    assignClicktoFollow(followingid);
                    assignMouseEntertoFollow(followingid);
                    assignMouseLeavetoFollow(followingid);
                    console.log("Assigned Event to button with id:" + followingid);

                }

                if(data.length==20 || data.length==30) {
                    if(action=="User Tweets") {
                        var timestamp_row = "<tr style='display: none' id='getmorerows'><td>" +  data[19].timestamp + "</td></tr>";
                        $('#contentTable').append(timestamp_row);

                    }
                    else if(action=="Tweets") {
                        var timestamp_row = "<tr style='display: none' id='getmorerows'><td>" +  data[19].timestamp + "</td></tr>";
                        $('#contentTable').append(timestamp_row);

                    }
                    else if(action=="Following") {

                        var lastemail_row = "<tr style='display: none' id='getmorerows'><td>" +  data[29].email + "</td></tr>";
                        $('#contentTable').append(lastemail_row);

                    }
                    else if(action=="Followers") {
                        var lastemail_row = "<tr style='display: none' id='getmorerows'><td>" +  data[29].email + "</td></tr>";
                        $('#contentTable').append(lastemail_row);

                    }
                }
                console.log("Got data from the server\n");
                console.log("After getting data\n" + $(window).height() + $(window).scrollTop() + '\n' +$(document).height())
                gettingdata = false;
            }
        })
    }
}

function giveTimeDifference(tweetdate,currentdate) {
    var month = new Array(12);
    month[0]="Jan";month[1]="Feb";month[2]="Mar";month[3]="Apr";month[4]="May";month[5]="Jun";month[6]="Jul";month[7]="Aug";month[8]="Sep";month[9]="Oct";month[10]="Nov";month[11]="Dec";

    if(tweetdate.getDate() != currentdate.getDate()) {
        timedifference = tweetdate.getDate() + " " + month[tweetdate.getMonth()];
    }
    else if(tweetdate.getHours() != currentdate.getHours()){

        timedifference =   currentdate.getHours() - tweetdate.getHours() + "h ago";
    }
    else if(tweetdate.getMinutes() != currentdate.getMinutes()) {

        timedifference = currentdate.getMinutes() -tweetdate.getMinutes() + "m ago";
    }
    else {
        timedifference = currentdate.getSeconds() - tweetdate.getSeconds() + "s ago";
    }

    return timedifference;
}