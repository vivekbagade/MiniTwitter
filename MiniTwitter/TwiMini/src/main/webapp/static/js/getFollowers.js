/**
 * Created with IntelliJ IDEA.
 * User: vivek
 * Date: 8/6/13
 * Time: 10:04 PM
 * To change this template use File | Settings | File Templates.
 */
function getFollowers(email){
    var bas64email = $.base64('encode',email);
    var url = '/followers/' + bas64email;
    console.log(url);
    $.ajax({
        url: url,
        type: "GET",
        dataType: 'json',
        success: function(data) {
            console.log(data.length);
            var content="";
            for(var i=0;i<data.length;i++) {

                content += '<tr><td><b>' + data[i].email + '</b></td></tr>';
            }
            if(content.length>0) {
                $('#contentTable tbody').html(content);
                $('#contentTitle').html("Followers");
            }
            else {
                content = '<tr><td><b>Currently not being followed by anyone<tr></td></b>';
                $('#contentTable tbody').html(content);
                $('#contentTitle').html("Followers");

            }

            $('#contentTable').find('#getmorerows').remove();
            if(data.length==30) {
                var timestamp_row = "<tr style='display: none' id='getmorerows'><td>" +  data[29].email + "</td></tr>";
                $('#contentTable').append(timestamp_row);
            }
        },
        error:function(data,status,er) {
            console.log("error: "+data+" status: "+status+" er:"+er);
        }
    })
}