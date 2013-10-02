<%--
  Created by IntelliJ IDEA.
  User: vivek
  Date: 8/5/13
  Time: 8:56 PM
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
        </div>
        </div>

        <div class="span9">

            <div class="hero-unit">
                <p id="welcometext"><b>${name}</b></p>
                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <th>
                            ${tweetCount} <a id="showMyTweets" href="#">Tweets</a>
                        </th>
                        <th>
                            ${followingCount} <a id="showWhoseFollowingMe" href="#">Followers</a>
                        </th>
                        <th>
                            ${subscriptionCount} <a id="showWhoIAmFollowing" href="#">Following</a>
                        </th>
                        <th>
                            <input class='btn btn-primary' type='submit' id='followButton'/>
                        </th>
                    </tr>
                    </thead>
                </table>
            </div>
            <div class="row-fluid">
                <div class="span12">
                    <table class="table"  id="contentTable">
                        <thead>
                        <tr>
                            <th id="contentTitle">
                                User Tweets
                            </th>
                        </tr>
                        </thead>
                        <tbody id="contentBody">
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
    $(document).ready(function(){
        $("#profilepic").attr("src","${profilepiclocation}");

        showmytweets("${email}");
        $('#showWhoseFollowingMe').click(function()
        {
            getFollowers("${email}");
        })
        $('#showWhoIAmFollowing').click(function()
        {
            showwhoIAmFollowing("${email}");
        })
        $('#showMyTweets').click(function()
        {
            showmytweets("${email}");
        })
    })
</script>

<script>
    var gettingdata= false;
    $(document).ready(function() {
        $(window).scroll(function(e) {
            infiniteScroll("${email}");
        })
    })
</script>

<script>
    $(document).ready(function() {
        var bas64email = $.base64('encode', $.cookie('email'));
        var bas64Oemail = $.base64('encode',"${email}");
        var url = '/isfollower?email=' + bas64email + '&otherEmail=' + bas64Oemail;
        console.log(url);
        $.ajax({
            url: url,
            type: "GET",
            dataType: 'text',
            success: function(data) {
                console.log("Success");
                $('#followButton').val(data);

                assignClicktoFollow('followButton',"${email}");
                console.log("Click event assigned to button\n");
                if(data==='Following')
                {
                    assignMouseEntertoFollow('followButton');
                    assignMouseLeavetoFollow('followButton');
                }
            },
            error:function(data,status,er) {
                console.log("error: "+data+" status: "+status+" er:"+er);
            }
        })
    })
</script>