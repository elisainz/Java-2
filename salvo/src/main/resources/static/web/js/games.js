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