<%--
  Created by IntelliJ IDEA.
  User: vivek
  Date: 8/6/13
  Time: 8:29 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="brand" href="#">Mini Twitter</a>

            <div class="nav-collapse collapse">
                <ul class="nav">
                    <li class="active"><a id="Home" href="#">Home</a></li>
                    <li><a href="#" id="Profile">Profile</a></li>
                    <li><a href="#" id="Logout">Logout</a></li>
                </ul>
                <form class="navbar-form pull-right" id="searchForm">
                    <input class="span2" type="text" placeholder="Search Query" id="search">
                    <button type="submit" class="btn" >Search</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        var email=$.cookie('email');
        var token=$.cookie('token')
        $('#Home').click(function() {
            getHome(email,token);
        })

        $('#Profile').click(function() {
            var bas64email = $.base64('encode',$.cookie('email'));
            var url = '/user/' + bas64email;
            window.location.href = url;
        })

        $('#Logout').click(function() {
            logoutUser($.cookie('email'),$.cookie('token'));
        })
    })
    function getHome(email,token)
    {
        var bas64email = $.base64('encode',email);
        var url = "/home?" + "token=" + token + "&email=" + bas64email;
        window.location.href = url;
    }
</script>

<script>
    $(document).ready(function(){
        var users=[];
        $("#search").typeahead({
            source: function(query, process){
                $.debounce(popet(query,process),500);}
        });
    })
    function popet ( query, process){
        $.get('/search/'+$.base64('encode',query), function(data){
            users=data;
            process(users);
        });

    }
</script>

<script>
    $('#searchForm').submit(function(e)
    {
        e.preventDefault();
        //console.log($('#search').val());
        var url = "/search/" + $.base64('encode',$('#search').val());
        console.log(url);
        $.ajax({
            url: url,
            type: "GET",
            dataType: 'json',
            success: function(data) {
                console.log(data.length);
                var content="";
                for(var i=0;i<data.length;i++) {
                    content += '<tr onclick="doSomething(this)" onmouseover="" style="cursor: pointer;"><td><b>' + data[i] + '</b></td></tr>';
                }
                if(content.length>0) {
                    $('#contentTable tbody').html(content);
                    $('#contentTitle').html("<h2>Search Results</h2>");
                }
                else {
                    var content = '<tr><td><b>No such user</b></td></tr>'
                    $('#contentTable tbody').html(content);
                    $('#contentTitle').html("<h2>Search Results</h2>");

                }
                $('#contentTable').find('#getmorerows').remove();
            }
        })
    });
    function doSomething(inst)
    {
        var email=inst.innerText;
        console.log(email);
        var url = "/user/" + $.base64('encode', email);
        window.location.href=url;
    }
</script>
