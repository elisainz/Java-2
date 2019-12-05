var tabla =""

$(function() {
   loadDataLeaderboard();
   $("#logOut").hide();
});



//--------------------------------game-table--------------------------------------------

 //function createGameTable () {
//$.get("/api/games")
//.done(
//    function(dataGames) {
 //   console.log(dataGames)
 //   tabla = "<thead> <tr><th> GAME ID </th> <th> CREATED </th> <th> PLAYER 1 </th> <th> PLAYER 2 </th> <th> GAME ACTIONS </th> </tr> </thead> "
  //  dataGames.games.forEach(function (game) {
    //         tabla += "<tr>"
     // tabla += "<td>" + game.id + "</td><td>" +  game.created  + "</td><td>" +   + "</td>";
    //})
  // tabla += "</tbody>"
  //return tabla;
  //})
//document.getElementById("general-game-table").innerHTML = tabla;
//}

function tablaGames (){
$.get("/api/games")
.done(function(data){
app.games=data.games;
app.user == data.player;
})
}
tablaGames ();

var app = new Vue({
  el: '#app',
  data: {
  games : [],
  user :"",
  }
})



/*function updateGameView (data) {
    let htmlList = data.map (function(game) {

       return  '<tr> <td>' + game.id + '</td> <td>' + game.created + '</td> <td>' + game.created + '</td> </td>' + '</td> <td>' + game.created + '</td> <tr>'     ;
         }).join('');



document.getElementById("general-game-table").innerHTML = htmlList;
} */

/*
function loadDataGames () {
 $.get("/api/games")
        .done(function(data) {
           updateGameView(data);
         })
         .fail(function( jqXHR, textStatus ) {
           alert( "Failed: " + textStatus );
          });
}

*/


//--------------------------------leaderboard--------------------------------------------

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



//--------------------------------sign in--------------------------------------------

$(function () {
    $('.submitbutton').click(function () {
        submitButton = $(this).attr('name')
    });
    $(".sign-up-button").click(function(){
    var request = {
                 userName: $("#username").val(),
                 password: $("#password").val()
                 };
        $.post("/api/players", request)
        .done(function(){
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
                                var player = data.player.email;
                                $("#formularioLogin").hide();
                                $("#logOut").show();
                                $("#playerLoggueado").text("User: " + player);
                                })
                        })
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
                    var player = data.player.email;
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

function createGame(){
                     $.post("/api/games")
                         .done(function(data){
                         window.location.href = 'game.html?gp=' + data.gpid;
                         })
                     }

function joinGame(gameId){
            $.post("/api/games/" + gameId + "/players")
                .done(function(data){
                    alert("Success: You're in");
                    window.location.href = '/web/game.html?gp='+data.gpid;
                    })
                .fail(function(){
                    alert("Failed");
                })
                }

function returnToGame (gamePlayers){
            var gamePlayerId = 0;
            if(gamePlayers[0].player.email == app.user.email){
                gamePlayerId = gamePlayers[0].gpid
            } else {
                gamePlayerId = gamePlayers[1].gpid
            }
            window.location.href = 'game.html?gp=' + gamePlayerId;
        }

