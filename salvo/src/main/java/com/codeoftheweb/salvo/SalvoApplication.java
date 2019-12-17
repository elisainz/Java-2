package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository,
                                      GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
        return (args) -> {
            // save a couple of customers
            Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
            Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
            Player player3 = new Player("kim_bauer@ctu.gov", passwordEncoder().encode("kb"));
            Player player4 = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));


            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);

            Date date1 = new Date(); //alt intro p Date importar clase
            Date date2 = Date.from(date1.toInstant().plusSeconds(3600)); //diferencia una hora con respecto a la otra
            Date date3 = Date.from(date2.toInstant().plusSeconds(3600));

            Game game1 = new Game(date1);
            Game game2 = new Game(date2);



            gameRepository.saveAll(Arrays.asList(game1, game2));


            GamePlayer gameplayer1 = new GamePlayer(date1, game1, player1); //pruebas
            GamePlayer gameplayer2 = new GamePlayer(date1, game1, player2);
            GamePlayer gameplayer3 = new GamePlayer(date1, game2, player3);
            GamePlayer gameplayer4 = new GamePlayer(date1, game2, player4);

            gamePlayerRepository.saveAll(Arrays.asList(gameplayer1, gameplayer2, gameplayer3, gameplayer4));


            Ship ship1 = new Ship("Destroyer", gameplayer1, new ArrayList<String>(Arrays.asList("A1", "A2")));
            Ship ship2 = new Ship("Destroyer", gameplayer1, new ArrayList<String>(Arrays.asList("C6", "C7")));
            Ship ship3 = new Ship("Submarine", gameplayer2, new ArrayList<String>(Arrays.asList("E1", "F1", "G1")));
            Ship ship4 = new Ship("Patrol Boat", gameplayer2, new ArrayList<String>(Arrays.asList("B4", "B5")));

            shipRepository.saveAll(Arrays.asList(ship1, ship2, ship3, ship4));


            Salvo salvo1t1 = new Salvo(gameplayer1, new ArrayList<String>(Arrays.asList("E1", "F1", "G1")), 1);
            Salvo salvo2t1 = new Salvo(gameplayer1, new ArrayList<String>(Arrays.asList("C", "C10")), 1);
            Salvo salvo3t1 = new Salvo(gameplayer2, new ArrayList<String>(Arrays.asList("A1", "A2")), 2);
            Salvo salvo4t2 = new Salvo(gameplayer2, new ArrayList<String>(Arrays.asList("B5", "B6")), 2);


            salvoRepository.saveAll(Arrays.asList(salvo1t1, salvo2t1, salvo3t1, salvo4t2));


            Score score1 = new Score(1, game1, player1);
            Score score2 = new Score(0, game1, player2);
            Score score3 = new Score(0.5, game2, player2);
            Score score4 = new Score(0.5, game2, player3);



            scoreRepository.saveAll(Arrays.asList(score1, score2, score3, score4));

        };
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName -> {
            Player player = playerRepository.findByUserName(inputName);
            if (player != null) {
                return new User(player.getUserName(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/web/games.html*").permitAll()
                .antMatchers("/web/css/*").permitAll() //**hacen referencia a todas las carpetas dentro de css
                .antMatchers("/web/js/*").permitAll()
                .antMatchers("/web/grid.html", "/web/grid.png", "/web/placeShipsGrid.html*").hasAuthority("USER")
                .antMatchers("/web/dist/**").permitAll()
                .antMatchers("/api/leaderboard").permitAll()
                .antMatchers("/api/games", "/api/login").permitAll()
                .antMatchers("/api/players").permitAll()
                .antMatchers("/api/game_view/*", "/web/game.html*", "/api/games/*/players", "/api/games/players/*/ships").hasAuthority("USER")
                .antMatchers("/rest/*").hasAuthority("ADMIN")
                .anyRequest().denyAll();

        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());


    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}

