/* Metodos propios de gridstack:
https://github.com/gridstack/gridstack.js/tree/develop/doc
*/
$(() => loadGrid())
const loadGrid = function () {
    var options = {

        width: 10,
        height: 10,
        verticalMargin: 0,
        cellHeight: 45,
        disableResize: true,
		float: true,
        disableOneColumnMode: true,
        staticGrid: false,
        animate: true
    }
    //inicializacion de la matriz
    $('.grid-stack').gridstack(options);

    grid = $('#grid').data('gridstack');


    grid.addWidget($('<div id="patrol_boat"><div class="grid-stack-item-content patrol_boatHorizontal"></div><div/>'),
        0, 1, 2, 1);

    grid.addWidget($('<div id="carrier"><div class="grid-stack-item-content carrierHorizontal"></div><div/>'),
        1, 5, 5, 1);

    grid.addWidget($('<div id="battleship"><div class="grid-stack-item-content battleshipHorizontal"></div><div/>'),
        3, 1, 4, 1);

    grid.addWidget($('<div id="submarine"><div class="grid-stack-item-content submarineVertical"></div><div/>'),
        8, 2, 1, 3);

    grid.addWidget($('<div id="destroyer"><div class="grid-stack-item-content destroyerHorizontal"></div><div/>'),
        7, 8, 3, 1);


    //createGrid construye la estructura de la matriz
    createGrid(11, $(".grid-ships"), 'ships')

    //Inicializo los listeneners para rotar los barcos
    //representa la cantidad de celdas que ocupa determinado barco
    rotateShips("carrier", 5)
    rotateShips("battleship", 4)
    rotateShips("submarine",3)
    rotateShips("destroyer", 3)
    rotateShips("patrol_boat",2)

    listenBusyCells('ships')
    $('.grid-stack').on('change', () => listenBusyCells('ships'))

}



const createGrid = function(size, element, id){

    let wrapper = document.createElement('DIV')


    wrapper.classList.add('grid-wrapper')


    for(let i = 0; i < size; i++){
        //row: <div></div>
        let row = document.createElement('DIV')
        //row: <div class="grid-row"></div>
        row.classList.add('grid-row')
        //row: <div id="ship-grid-row0" class="grid-wrapper"></div>
        row.id =`${id}-grid-row${i}`
        /*
        wrapper:
                <div class="grid-wrapper">
                    <div id="ship-grid-row-0" class="grid-row">

                    </div>
                </div>
        */
        wrapper.appendChild(row)

        for(let j = 0; j < size; j++){

            let cell = document.createElement('DIV')

            cell.classList.add('grid-cell')

            if(i > 0 && j > 0){

                cell.id = `${id}${i - 1}${ j - 1}`
            }

            if(j===0 && i > 0){

                let textNode = document.createElement('SPAN')

                textNode.innerText = String.fromCharCode(i+64)

                cell.appendChild(textNode)
            }

            if(i === 0 && j > 0){

                let textNode = document.createElement('SPAN')

                textNode.innerText = j

                cell.appendChild(textNode)
            }

            row.appendChild(cell)
        }
    }

    element.append(wrapper)
}

/*para rotar los barcos*/
const rotateShips = function(shipType, cells){

        $(`#${shipType}`).click(function(){
            document.getElementById("alert-text").innerHTML = `Rotaste: ${shipType}`
            console.log($(this))
            //Establecemos nuevos atributos para el barco que giramos
            let x = +($(this).attr('data-gs-x'))
            let y = +($(this).attr('data-gs-y'))



        if($(this).children().hasClass(`${shipType}Horizontal`)){
        	if(grid.isAreaEmpty(x,y+1,1,cells) || y + cells < 10){
	            if(y + cells - 1 < 10){

	                grid.resize($(this),1,cells);
	                $(this).children().removeClass(`${shipType}Horizontal`);
	                $(this).children().addClass(`${shipType}Vertical`);
	            } else{

	            		grid.update($(this), null, 10 - cells)
	                	grid.resize($(this),1,cells);
	                	$(this).children().removeClass(`${shipType}Horizontal`);
	                	$(this).children().addClass(`${shipType}Vertical`);
	            }


            }else{
            		document.getElementById("alert-text").innerHTML = "A ship is blocking the way!"
            }

        //Este bloque se ejecuta si el barco que queremos girar esta en vertical
        }else{

            if(x + cells - 1  < 10){
                grid.resize($(this),cells,1);
                $(this).children().addClass(`${shipType}Horizontal`);
                $(this).children().removeClass(`${shipType}Vertical`);
            } else{

                grid.update($(this), 10 - cells)
                grid.resize($(this),cells,1);
                $(this).children().addClass(`${shipType}Horizontal`);
                $(this).children().removeClass(`${shipType}Vertical`);
            }

        }
    });

}

//Bucle que consulta por todas las celdas para ver si estan ocupadas o no
const listenBusyCells = function(id){

    for(let i = 0; i < 10; i++){
        for(let j = 0; j < 10; j++){
            if(!grid.isAreaEmpty(i,j)){
                $(`#${id}${j}${i}`).addClass('busy-cell').removeClass('empty-cell')
            } else{
                $(`#${id}${j}${i}`).removeClass('busy-cell').addClass('empty-cell')
            }
        }
    }
}

const obtenerPosicion = function (shipType) {
    var ship = new Object();
    ship["name"] = $("#" + shipType).attr('id');
    ship["x"] = $("#" + shipType).attr('data-gs-x');
    ship["y"] = $("#" + shipType).attr('data-gs-y');
    ship["width"] = $("#" + shipType).attr('data-gs-width');
    ship["height"] = $("#" + shipType).attr('data-gs-height');
    ship["positions"] = [];
    if (ship.height == 1) {
        for (i = 1; i <= ship.width; i++) {
            ship.positions.push(String.fromCharCode(parseInt(ship.y) + 65) + (parseInt(ship.x) + i))
        }
    } else {
        for (i = 0; i < ship.height; i++) {
            ship.positions.push(String.fromCharCode(parseInt(ship.y) + 65 + i) + (parseInt(ship.x) + 1))
        }
    }
    var objShip = new Object();
    objShip["ship"] = ship.shipType;
    objShip["shipLocation"] = ship.shipLocation;
    return objShip;
}

function getParameterByName(name) {
  var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}
getParameterByName('gp');
var gpid = Number(getParameterByName('gp'));

function placeShips(gpid){
    var shipTypes = ["battleship","carrier","destroyer","patrol_boat","submarine"];
        var placedShips = [];
        for(var i=0; i<shipTypes.length; i++){
        placedShips[i] = obtenerPosicion(shipTypes[i]);
        }

    $.post({
      url: "/api/games/players/" + gpid + "/ships",
      data: JSON.stringify(placedShips),
      dataType: "text",
      contentType: "application/json"
    })
    .done(function (response, status, jqXHR) {
      alert( "Ships added: " + response );
    })
    .then(function(){
    window.location.href = 'game.html?gp=' + gpid;
    })
    .fail(function (jqXHR, status, httpError) {
      alert("Failed to add ships: " + status + " " + httpError);
    })
    }

 function getParameterByName(name) {
   var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
   return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
 }
 getParameterByName('gp');
 var gpid = Number(getParameterByName('gp'));