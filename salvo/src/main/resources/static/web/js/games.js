var tabla =""
var games;
var gamesData;
var app = new Vue({
  el: '#app',
  data: {
  games : [],
  user : "guest",
  }
})

$(function() {
   tablaGames();
   loadDataLeaderboard();



 //falta que vuelva a preguntar si esta logueado al refrescar para que no muestre el formulario de Log In de nuevo
});



//--------------------------------game-table--------------------------------------------


function tablaGames (){
$.get("/api/games")
.done(function(data){
games=data.games;
app.games=data.games;
gamesData = data;
if(gamesData.player == "guest"){
   $("#formularioLogin").show();
   $("#logout").hide();
   $("#creategame").hide();
   $(".joingame").hide();

    }else{
        $("#formularioLogin").hide();
         $("#logout").show();
    }
})
}






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
                                app.user = data.player.email;

                                actualPlayer = player;
                                $("#formularioLogin").hide();
                                $("#logout").show();
                                $(".joingame").show();
                                $(".returngame").show();
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
                    actualPlayer= player;
                    app.user = player;
                    $("#formularioLogin").hide();
                    $("#logout").show();
                    $("#creategame").show();
                    $(".joingame").show();
                    $(".returngame").show();
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
function logOut(){
          $.post("/api/logout")
              .done(function(){
              alert("Logged out");
              $("#formularioLogin").show();
              $("#logout").hide();
              $("#creategame").hide();
              $(".joingame").hide();
              $(".returngame").hide();
              })
      }



//-----------------------------------------------------
function createGame(){
                     $.post("/api/games")
                         .done(function(data){
                         window.location.href = '/web/placeShipsGrid.html?gp=' + data.gpid;
                         alert("Game Created");
                         })
                     }

function joinGame(gameId){
            $.post("/api/games/" + gameId + "/players")
                .done(function(data){
                    alert("Success: You're in");
                    window.location.href = '/web/placeShipsGrid.html?gp=' + data.gpid;
                    })
                .fail(function(){
                    alert("Failed");
                })


function returnToGame (gamePlayers){
            var gamePlayerId = 0;
            if(gamePlayers[0].player.email == app.user.email){
                gamePlayerId = gamePlayers[0].gpid
            } else {
                gamePlayerId = gamePlayers[1].gpid
            }
            window.location.href = 'game.html?gp=' + gamePlayerId;
        }
}

/*function placeShips(gamePlayerId){
    $.post({
      url: "api/games/players/" + gamePlayerId + "/ships",
      data: JSON.stringify([{ shipType: shipType, shipLocations: shipLocations}]),
      dataType: "text",
      contentType: "application/json"
    })
    .done(function (response, status, jqXHR) {
      alert( "Ships added: " + response );
    })
    .fail(function (jqXHR, status, httpError) {
      alert("Failed to add ships: " + status + " " + httpError);
    }) */