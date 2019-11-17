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
    public Map<String, Object> getGameView(@PathVariable long id) {
        return gameViewDTO(gamePlayerRepository.getOne(id));
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


    @RequestMapping(path = "/players", method = RequestMethod.POST)
    // ResponseEntity da flexibilidad adicional para definir encabezados de respuesta HTTP
    public ResponseEntity<Map<String, Object>> register( //register es el nombre que le doy al metodo para crear un nuevo usuario(Player) a partir de la ruta ("/api/"players")
            @RequestParam String userName, @RequestParam String password) {

        if (userName.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>( createMap("error", "Missing data"), HttpStatus.BAD_REQUEST);
        } else if (playerRepository.findByUserName(userName) != null) {
            return new ResponseEntity<>(createMap ("error", "Name already in use"), HttpStatus.FORBIDDEN);
        }

        Player player = new Player(userName, passwordEncoder.encode(password)); //hay que hacerle autowired al encoder
        playerRepository.save(player);
        return new ResponseEntity<>(createMap("success",player.getPlayerDto()), HttpStatus.CREATED);
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