package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    private boolean guest (Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping("/games")
    public Map<String,Object> getGames(Authentication authentication){
        Map<String,Object> dto = new LinkedHashMap<>(); //LinkedHashMap is just like HashMap with an additional feature of maintaining an order of elements inserted into it.

    //public List<Map<String, Object>> getGames() {
        //return gameRepository.findAll()
            //    .stream()
             //   .map(Game::getDto)
             //   .collect(Collectors.toList());
    //}

      if (guest(authentication)){
        dto.put("player", "guest");
    }else{
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
    @RequestMapping("/leaderboard")
            public List<Map<String, Object>> getPlayers() {
            return playerRepository.findAll()
                    .stream()
                    .sorted(Comparator.comparing(Player::getTotalScore).reversed())
                    .map(Player::getLeaderboardDto)
                    .collect(toList());
    }
}