<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: vikramgoyal
  Date: 7/22/13
  Time: 7:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title></title>
    <link rel="stylesheet" href="/static/css/bootstrap.css" type="text/css"/>
    <link rel="stylesheet" href="/static/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="/static/css/bootstrap-responsive.css" type="text/css">
</head>
<body style="background: url('/static/img/templatebackground.jpg');background-repeat:no-repeat; background-size: cover; -webkit-background-size: cover;
    -moz-background-size: cover;
    -o-background-size: cover;">

<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="/static/js/bootstrap.min.js"></script>
<script src="/static/js/bootstrap-transition.js"></script>
<script src="/static/js/bootstrap-alert.js"></script>
<script src="/static/js/bootstrap-modal.js"></script>
<script src="/static/js/bootstrap-dropdown.js"></script>
<script src="/static/js/bootstrap-scrollspy.js"></script>
<script src="/static/js/bootstrap-tab.js"></script>
<script src="/static/js/bootstrap-tooltip.js"></script>
<script src="/static/js/bootstrap-popover.js"></script>
<script src="/static/js/bootstrap-button.js"></script>
<script src="/static/js/bootstrap-collapse.js"></script>
<script src="/static/js/bootstrap-carousel.js"></script>
<script src="/static/js/bootstrap-typeahead.js"></script>
<script src="/static/js/jquery.base64.js"></script>
<script src="/static/js/jquery.cookie.js"></script>
<script src="/static/js/shownewsfeed.js"></script>
<script src="/static/js/showmytweets.js"></script>
<script src="/static/js/whoIAmFollowing.js"></script>
<script src="/static/js/follow_unfollow_user.js"></script>
<script src="/static/js/logoutUser.js"></script>
<script src="/static/js/getFollowers.js"></script>
<script src="/static/js/inifiniteScroll.js"></script>

<%@include file="header.jsp" %>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span3">
            <div class="hero-unit" id="image">
                <img id="profilepic" height="300" width="300">
                <form:form commandName="ImageUploadForm" action="uploadimage.html" method="post" id="uploadimageform" enctype="multipart/form-data" >
                <table>
                    <tr>
                        <td><form:hidden path="email"/></td>
                    </tr>
                    <tr>
                        <td><form:hidden path="token"/></td>
                    </tr>
                    <tr>
                        <td><div style="display: none"><form:input path="file" type="file" id="imageupload"/></div></td>
                    </tr>

                    <tr><td>
                        <input class="btn btn-primary" type="submit" value="Change Image" id="changeimage" style="display: none" />
                    </td></tr>
                </table>
                </form:form>
            </div>
            <div class="hero-unit">
                <b>Trending Words</b><br><br>
                <c:forEach items="${trends}" var="trendingword">
                    <b>${trendingword}</b><br>
                </c:forEach>
            </div>

        </div><!--/span-->
        <div class="span9">
            <div class="hero-unit">
                <p id="welcometext">Welcome,${name}</p>
                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <th>
                            ${tweetcount} <a id="showMyTweets" href="#">Tweets</a>
                        </th>
                        <th>
                            ${followingcount} <a id="showWhoseFollowingMe" href="#">Followers</a>
                        </th>
                        <th>
                            ${subscriptioncount} <a id="showWhoIAmFollowing" href="#">Following</a>
                        </th>
                    </tr>
                    </thead>
                </table>
                <div class="hero-unit" id="tweetbox">

                    <textarea class="span14" rows="3" id="tweetContent"></textarea>
                    <p>

                    <div id="charactersRemaining" style="display: inline">140</div>

                    <span class=pull-right><button class="btn btn-primary" type="submit" id="posttweet" disabled>Tweet</button></span>

                    </p>

                </div>
            </div>
            <div class="row-fluid">
                <div class="span12">
                    <table class="table" id="contentTable">
                        <thead>
                        <tr>
                            <th id="contentTitle">
                                User Tweets
                            </th>
                        </tr>
                        </thead>
                        <tbody id="contentBody">
                        <tr>
                            <td>
                                Tweet No1
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Tweet No2
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div><!--/row-->
        </div><!--/span-->
    </div><!--/row-->
    <div class="row-fluid">
        <div class="span3">
        </div>
    </div>
</div>
<script>
    $(document).ready(function() {
        showFeed($.cookie('email'));
        $("#profilepic").attr("src","${profilepiclocation}");
        $("#profilepic").attr("height","300");
        $("#profilepic").attr("height","300");
        $('#showMyTweets').click(function(){

            showmytweets($.cookie('email'));
        })
        $('#showWhoIAmFollowing').click(function(){
            showwhoIAmFollowing($.cookie('email'));
        })

        $("#profilepic").click(function() {
            $('#imageupload').click();



        })

        $('#imageupload').change(function() {
            console.log("You selected a file\n");
            var imageselected = $(this).val();
            if(imageselected.length>0) {
                console.log("You selected the file:" + imageselected);
                $('#changeimage').click();
             }
             else {
             console.log("Please select a file\n");
             }
        })

        $('#uploadimageform').bind('complete',function() {
            alert('Done');
            window.location.reload();
        })
    })
</script>

<script>
    $(document).ready(function(){
        $('#posttweet').click(function(){
;
            var email = $.cookie('email');

            var JsonP = {"token":$.cookie('token'),"email":email,"content":$('#tweetContent').val()};
            console.log(JSON.stringify(JsonP));
            $.ajax({
                url:'/tweets',
                type: "POST",
                data: JSON.stringify(JsonP),
                contentType: 'application/json',
                mimeType: 'application/json',

                success: function(data) {
                    console.log("Tweet Was successfully inserted");
                    $('#tweetContent').val("");
                },
                error:function(data,status,er) {
                    console.log("error: "+data+" status: "+status+" er:"+er);
                }
            })
        })
    })
</script>


<script>
    $(document).ready(function(){
        $('#showWhoseFollowingMe').click(function(){
            getFollowers($.cookie('email'));
        })
    })
</script>


<script>
    var gettingdata= false;
    $(document).ready(function() {
        $(window).scroll(function(e) {
            infiniteScroll($.cookie('email'));
        })
    })
</script>

<script>
    $(document).ready(function(){
        $('#tweetContent').keyup(function(){
            var charactersEntered = $('#tweetContent').val();
            var characterLength = charactersEntered.length;

            if(characterLength==0) {
                $('#posttweet').attr('disabled','disabled')
            }
            else if(characterLength>0) {
                $('#posttweet').removeAttr('disabled');
            }

            if(characterLength>140) {
                $('#tweetContent').val(charactersEntered.substr(0,140));
                $('#charactersRemaining').html("0");
            }
            else {
                var charactersRemaining = 140 - characterLength;
                $('#charactersRemaining').html(charactersRemaining);
            }

        })
    })
</script>
<script>
    var base64email=$.base64('encode',$.cookie('email'));
    var ws= new WebSocket("ws://172.16.155.82:8081/websocket/api?email="+base64email);
    ws.onopen= function(){
    }
    ws.onmessage= function(event){
        console.log("Message from socket: " + event.data);
        var str=event.data.split(":");
        var email=str[2];
        var tweet=str[1];
        if($('#contentTitle').text()==="Tweets")
        {
            console.log(email+" "+tweet);
            var content = '<tr><td><b>' + email + '</b><span class="pull-right"><b>1 s ago</b></span> <br>' + tweet + '</td></tr>';
            $('#contentTable').prepend(content);
        }
    }
</script>


</body>

</html>