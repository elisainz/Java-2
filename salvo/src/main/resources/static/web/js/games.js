$(function() {
    loadDataLeaderboard();
});



function updateView(data) {
    let htmlList = data.map(function (games) {

        return  '<tr> <td>' + games.name + '</td> <td>' + games.total + '</td> <td>' + games.won + '</td> </td>' + '</td> <td>' + games.lost + '</td> <td>' + games.tied + '</td> <tr>'     ; //+ ' ' + games.////???? .map(function(p) { return p.player.email}).join(',')  +'</li>';
    }).join('');

  document.getElementById("game-table").innerHTML = htmlList;
}

// load and display JSON sent by server for /players

function loadDataLeaderboard (){
    $.get("/api/leaderboard")
        .done(function(data) {
           updateView(data);
         })
         .fail(function( jqXHR, textStatus ) {
           alert( "Failed: " + textStatus );
          });
}

$(function() {
    $('.submitbutton').click(function () {
        submitButton = $(this).attr('name')
    });
    $(".sign-up-button").click(function(){
    var request = {
                 username: $("#username").val(),
                 password: $("#password").val()
                 };
        $.post("/api/players", request)
        .done(function(){
            alert("Success");
        })
        .fail(function(){
            alert("Error");
        })
    })
});
$('#login-form').on('submit', function (event) {
    event.preventDefault();
    if (submitButton == "login") {
    var request = {
                 username: $("#username").val(),
                 password: $("#password").val()
                 };
        $.post("/api/login", request)
            .done(function() {
             alert("Success");
                $('#loginSuccess').show();
                $("#username").val("");
                $("#password").val("");
                $.get("/api/games")
                    .done(function(data){
                    var player = data.players.email;
                    $("#formularioLogin").hide();
                    $("#logOut").show();
                    $("#playerLoggueado").text("User: " + player);
                    })
            })
            .fail(function() {
                alert("Error");
                $('#loginFailed').show();
                $("#username").val("");
                $("#password").val("");
                $("#username").focus();
            })
    }
});
function logout(){
          $.post("/api/logout")
              .done(function(){
              alert("Logged out");
              $("#formularioLogin").show();
              $("#logOut").hide();
              })
      }