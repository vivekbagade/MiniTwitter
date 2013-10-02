/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 7/30/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Created with IntelliJ IDEA.
 * User: vikramgoyal
 * Date: 7/30/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */

function showmytweets(email) {
    var base64email = $.base64('encode',email);
    var url = "/tweets/" + base64email;
    console.log(url);
    $.ajax({
        url: url,
        type: "GET",
        dataType: 'json',
        success: function(data) {
            console.log(data.length);
            var content="";
            for(var i=0;i<data.length;i++) {
                content += '<tr><td  type="text"><b>' + data[i].email + '</b><span class="pull-right"><b>' + giveTimeDifference(new Date(data[i].timestamp),new Date()) + '</b></span>' +
                    '<br>' + data[i].content + '</td></tr>';
            }
            if(content.length>0) {
                $('#contentTable tbody').html(content);
                $('#contentTitle').html("User Tweets");
            }
            else {
                var content = '<tr><td><b>Not tweeted yet</b></td></tr>'
                $('#contentTable tbody').html(content);
                $('#contentTitle').html("User Tweets");

            }

            $('#contentTable').find('#getmorerows').remove();

            if(data.length==20) {
                var timestamprow = "<tr style='display: none' id='getmorerows'><td>" +  data[9].timestamp + "</td></tr>";
                $('#contentTable').append(timestamprow);
            }
        },
        error:function(data,status,er) {
            console.log("error: "+data+" status: "+status+" er:"+er);
        }
    })
    //setInterval(checkTweetNotification,10000);
}




