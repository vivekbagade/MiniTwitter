



<%--
  Created by IntelliJ IDEA.
  User: vikramgoyal
  Date: 7/22/13
  Time: 1:16 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Bootstrap nested columns example</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <link rel="stylesheet" href="/static/css/bootstrap.css" type="text/css"/>
    <link rel="stylesheet" href="/static/css/bootstrap.min.css" type="text/css"/>
    <style type="text/css">

        .registration-form {display: none}

            /*html {
                background: url('/static/img/HomePage_background') no-repeat center center fixed;
                -webkit-background-size: cover;
                -moz-background-size: cover;
                -o-background-size: cover;
                background-size: cover;
            }*/

    </style>
</head>
<body style="background: url('/static/img/background.png');background-repeat:no-repeat; background-size: cover; -webkit-background-size: cover;
    -moz-background-size: cover;
    -o-background-size: cover;">

<div class="container">
        <div class="row">
            <div class="span4 offset8">
                <div class="login-form" id="login-form" style="float: right">
                    <h2>Login</h2>
                    <form id="logindetails">
                        <fieldset>
                            <div class="alert" id="login_alertmessage" style="display: none">

                            </div>
                            <div class="clearfix">
                                <input type="text" placeholder="Email" id="email">
                            </div>
                            <div class="clearfix">
                                <input type="password" placeholder="Password" id="password">
                            </div>
                            <label class="checkbox">
                                <input type="checkbox">Remember Me
                            </label>
                            <input class="btn btn-primary" type="submit" id="validatecredentials" value="Sign in" />
                            <a id="newuser"> New User?</a>
                        </fieldset>
                    </form>
                </div>

                <div class="registration-form" id="registration-form" style="float: right">
                    <h2>Register</h2>
                    <form id="registrationform">
                        <fieldset>
                            <div class="alert" id="registration_alertmessage" style="display: none">

                            </div>

                            <div class="clearfix">
                                <input type="text" placeholder="Email" id="register_email">
                            </div>
                            <div class="clearfix">
                                <input type="text" placeholder="Name" id="register_name">
                            </div>
                            <div class="clearfix">
                                <input type="password" placeholder="Password" id="register_password">
                            </div>

                            <input class="btn btn-primary" id="registeruser" type="submit" value="Register">
                            <a id="signin">Sign-in</a>
                        </fieldset>
                    </form>
                </div>

            </div>
        </div>
</div>

        <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
        <script src="/static/js/bootstrap.min.js"></script>
        <script src="/static/js/bootstrap-alert.js"></script>
        <script src="/static/js/validation.js"></script>
        <script src="/static/js/jquery.base64.js"></script>

        <script>

            $('#logindetails').submit(function(e) {
                e.preventDefault();
                var credentials = {"email":$('#email').val(),"password":$('#password').val()};

                if(!emailValidation($('#email').val())) {
                    $('#login_alertmessage').show();
                    $('#login_alertmessage').html('<a class="close" data-dismiss="alert">×</a> <strong>Invalid E-Mail ID</strong>');
                }
                else if(!passwordvalidation($('#password').val())){
                    $('#login_alertmessage').show();
                    $('#login_alertmessage').html('<a class="close" data-dismiss="alert">×</a> <strong>Password cannot be null </strong>');
                }
                else {
                    $('#login_alertmessage').hide();

                $.ajax({
                    url: '/login',
                    type: "POST",
                    contentType: 'application/json',
                    data: JSON.stringify(credentials) ,
                    dataType: 'json',
                    mimeType: 'application/json',
                    success: function(data) {
                    if(data.result == "success") {

                            console.log(data.token);
                            var bas64email = $.base64('encode', data.email);

                            var url = "/home?" + "token=" + data.token + "&email=" + bas64email;

                            window.location.href = url;

                        }
                        else if(data.result == "failure") {
                                console.log("User did not give valid credentials");
                                $('#login_alertmessage').show();
                                $('#login_alertmessage').html('<a class="close" data-dismiss="alert">×</a> <strong>Wrong credentials</strong>');
                        }

                    },
                    error:function(data,status,er) {
                        console.log("error: "+data+" status: "+status+" er:"+er);
                    }
                })
                }
            })

        </script>

        <script>
            $(document).ready(function() {
                $('#registrationform').submit(function(e) {
                    e.preventDefault();
                    var registrationdetails = {"email":$('#register_email').val(),"password":$('#register_password').val(),"name":$('#register_name').val()};

                    if(!emailValidation($('#register_email').val())) {
                        $('#login_alertmessage').show();
                        $('#login_alertmessage').html('<a class="close" data-dismiss="alert">×</a> <strong>Invalid E-Mail ID</strong>');
                    }
                    else if(!passwordvalidation($('#register_password').val())){
                        $('#login_alertmessage').show();
                        $('#login_alertmessage').html('<a class="close" data-dismiss="alert">×</a> <strong>Password cannot be null </strong>');
                    }
                    else {
                        $('#login_alertmessage').hide();
                    $.ajax({
                        url: '/users',
                        type: "POST",
                        contentType: 'application/json',
                        data: JSON.stringify(registrationdetails) ,
                        dataType: 'json',
                        mimeType: 'application/json',
                        success: function(data) {
                            //console.log(data);
                            if(data.result==1) {
                                console.log("User successfully registered.");
                                        console.log(data.email);
                                console.log(data.token);
                                var bas64email = $.base64('encode', data.email);
                                window.location.href = "/home?token=" + data.token + "&email=" + bas64email;
                            }
                            else if(data.result==0) {
                                console.log("User could not be registered. Another user with the same Email is already registered");
                            }
                        },
                        error:function(data,status,er) {
                            console.log("error: "+data+" status: "+status+" er:"+er);
                            console.log("error: "+data+" status: "+status+" er:"+er);
                        }
                    })
                    }
//                $('#logindetails').preventDefault();
                })


            })


        </script>
        <script>
            $(document).ready(function(){
                $('#newuser').click(function(){
                    $('.login-form').hide();
                    //console.log("Login Hidden");
                    $('.registration-form').show();

                });
            })
        </script>
        <script>
            $(document).ready(function(){
                $('#signin').click(function(){
                    //console.log("Login Hidden");
                    $('.registration-form').hide();
                    $('.login-form').show();

                });
            })
        </script>


</body>
</html>











