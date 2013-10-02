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
 * Date: 7/29/13
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */




function showFeed(email) {

    var bas64email= $.base64('encode', email);
    var url = '/feed/'+ bas64email;
    console.log("Fetching feed for the first time" + url);
    $.ajax({
        url: url,
        type: "GET",
        dataType: 'json',
        success: function(data) {
            //console.log(data);
            console.log(data.length);
            var content="";
            for(var i=0;i<data.length;i++) {
                content += '<tr><td><b>' + data[i].email + '</b><span class="pull-right"><b>' + giveTimeDifference(new Date(data[i].timestamp),new Date()) + '</b></span><br>'
                    + data[i].content + '</td></tr>';
            }

            $('#contentTable').find('#getmorerows').remove();

            if(content.length>0) {
                $('#contentTable tbody').html(content);
                $('#contentTitle').html("Tweets");
            }
            else {
                content = '<tr><td><b>You need to follow others before their tweets become visible in your feed.<tr></td></b>';
                $('#contentTable tbody').html(content);
                $('#contentTitle').html("Tweets");

            }

            if(data.length==20) {
                var timestamp_row = "<tr style='display: none' id='getmorerows'><td>" +  data[9].timestamp + "</td></tr>";
                $('#contentTable').append(timestamp_row);
            }
        },
        error:function(data,status,er) {
            console.log("error: "+data+" status: "+status+" er:"+er);
        }
    })
}