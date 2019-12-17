package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api")

public class SalvoController {

    @Autowired //sirve para enlazar las interfases
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ShipRepository shipRepository;


    private boolean guest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>(); //dto lo pasa a json


        if (guest(authentication)) {
            dto.put("player", "guest");
        } else {
            Player player = playerRepository.findByUserName(authentication.getName());
            dto.put("player", player.getPlayerDto());
        }

        dto.put("games", gameRepository.findAll()
                .stream()
                .map(Game::getDto)
                .collect(toList()));
        return dto;
    }

    @RequestMapping("/game_view/{id}")
    public ResponseEntity <Map<String, Object>> getGameView(@PathVariable long id, Authentication authentication) {


        GamePlayer gamePlayer = gamePlayerRepository.findById(id).orElse(null);
        if (guest(authentication)) {
           return new ResponseEntity<> (createMap("error", "You need to log in first"), HttpStatus.UNAUTHORIZED);
        }
        Player player = playerRepository.findByUserName(authentication.getName());
        if (gamePlayer.getPlayer().getId() != player.getId()) {
            return new ResponseEntity<> (createMap("error", "This isn't your game LAKAAAA"), HttpStatus.FORBIDDEN);
        }
        Game game =  gamePlayer.getGame();
        if(game.getGamePlayers().size() == 2) {
            return new ResponseEntity<>(createMap("error", "Game's already full."), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(gameViewDTO(gamePlayer), HttpStatus.ACCEPTED);


    } //long porque id es numerico


    private Map<String, Object> gameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = gamePlayer.getGame().getDto();

        dto.put("ships", getShipList(gamePlayer.getShips()));

        dto.put("salvoes", gamePlayer.getGame()
                .getGamePlayers()
                .stream()
                .flatMap(gp -> gp.getSalvoes().stream().map(salvo -> salvo.getDto()))
                .collect(Collectors.toList())
        );


        return dto;
    }

    private List<Map<String, Object>> getShipList(Set<Ship> ships) {

        return ships
                .stream()
                .map(Ship::getDto)
                .collect(Collectors.toList());
    }



//------------create a new player------------
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    // ResponseEntity da flexibilidad adicional para definir encabezados de respuesta HTTP
    public ResponseEntity<Map<String, Object>> register( //register es el nombre que le doy al metodo para crear un nuevo usuario(Player) a partir de la ruta ("/api/"players")
            @RequestParam String userName, @RequestParam String password) {

        if (userName.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>( createMap("error", "Missing data. Please complete the fields"), HttpStatus.BAD_REQUEST);
        } else if (playerRepository.findByUserName(userName) != null) {
            return new ResponseEntity<>(createMap ("error", "Username is already taken"), HttpStatus.FORBIDDEN);
        }

        Player player = new Player(userName, passwordEncoder.encode(password)); //hay que hacerle autowired al encoder
        playerRepository.save(player);
        return new ResponseEntity<>(createMap("success",player.getPlayerDto()), HttpStatus.CREATED);
    }


    //-------------create a new game-----------
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication){
        if(isGuest(authentication)){
            return new ResponseEntity<>(createMap("error", "Can't create game being a guest. You need to log in" ),HttpStatus.FORBIDDEN);
        }else{

            Game game = gameRepository.save(new Game(new Date()));
            Player player = playerRepository.findByUserName(authentication.getName());

            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(new Date(), game, player));
            return new ResponseEntity<>(createMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);

        }
    }
//para determinar que la persona esta logueada
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }



//------------------join an existent game---------------
@RequestMapping(path = "/games/{id}/players", method = RequestMethod.POST)
public ResponseEntity<Map<String, Object>>joinGame(@PathVariable Long id, Authentication authentication){
    Game game = gameRepository.findById(id).orElse(null);
    if(isGuest(authentication)){
        return new ResponseEntity<>(createMap("error", "You need to log in first"), HttpStatus.UNAUTHORIZED);
    }
    Player player = playerRepository.findByUserName(authentication.getName());
    if(game == null){
        return new ResponseEntity<>(createMap("error", "This game doesn't exist"), HttpStatus.FORBIDDEN);
    }
    if(game.getGamePlayers().size() == 2) {
        return new ResponseEntity<>(createMap("error", "Game's already full"), HttpStatus.FORBIDDEN);
    }
    if(game.getGamePlayers().stream().map(gp -> gp.getPlayer().getUserName()).collect(Collectors.toList()).contains(authentication.getName())){
        return new ResponseEntity<>(createMap("error", "You're already in this game"), HttpStatus.FORBIDDEN);
    }

    GamePlayer gamePlayer = new GamePlayer(new Date(), game, player);
    gamePlayerRepository.save(gamePlayer);

    return new ResponseEntity<>(createMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
}

    @RequestMapping(path = "/games/players/{gpid}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placedShips(@PathVariable Long gpid, Authentication authentication, @RequestBody List<Ship> ships){
        GamePlayer gamePlayer = gamePlayerRepository.findById(gpid).orElse(null);
        Player player = playerRepository.findByUserName(authentication.getName());
        if(isGuest(authentication)){
            return new ResponseEntity<>(createMap("error", "Access denied, please log in. "), HttpStatus.UNAUTHORIZED);
        }
        if(gamePlayer == null) {
            return new ResponseEntity<>(createMap("error", "There's no game player."), HttpStatus.UNAUTHORIZED);
        }
        if(player.getId() != gamePlayer.getPlayer().getId()){
            return new ResponseEntity<>(createMap("error", "You're not this player"), HttpStatus.UNAUTHORIZED);
        }
        if(gamePlayer.getShips().size() > 0){
            return new ResponseEntity<>(createMap("error", "Ships are already placed."), HttpStatus.FORBIDDEN);
        }
        ships.stream().map(ship ->
                shipRepository.save(new Ship (ship.getType(), ship.getShipLocations(), gamePlayer))
        ).collect(Collectors.toList());
        return new ResponseEntity<>(createMap("success", "Ships placed!"), HttpStatus.CREATED);
    }


    @RequestMapping("/leaderboard")
    public List<Map<String, Object>> getPlayers() {
        return playerRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Player::getTotalScore).reversed())
                .map(Player::getLeaderboardDto)
                .collect(toList());
    }

    //reemplaza a inicializar un map y cargarle.. make dto, etc
    public Map<String, Object> createMap (String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }





}