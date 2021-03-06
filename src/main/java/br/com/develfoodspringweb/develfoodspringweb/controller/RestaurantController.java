package br.com.develfoodspringweb.develfoodspringweb.controller;


import br.com.develfoodspringweb.develfoodspringweb.controller.dto.RestaurantDto;
import br.com.develfoodspringweb.develfoodspringweb.controller.form.RestaurantForm;
import br.com.develfoodspringweb.develfoodspringweb.models.Restaurant;
import br.com.develfoodspringweb.develfoodspringweb.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    /**
     * Function with GET method to do make a query with the name of the restaurant as parameter.
     * @param nameRestaurant
     * @return
     * @author: Thomas B.P.
     */
    @GetMapping
    public RestaurantDto getRestaurantByName(@RequestParam String nameRestaurant){
        if(nameRestaurant == null){
            return null;
        }

        Optional<Restaurant> opt = restaurantRepository.findByName(nameRestaurant);
        if (!opt.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Restaurant name not found");
        }
        return RestaurantDto.convertToRestaurantDto(opt.get());
    }

    /**
     * Function with POST method to register new Restaurant while the function create the URI route and return the head HTTP location with the URL
     * @param restaurantForm
     * @param uriComponentsBuilder
     * @return
     * @author: Thomas B.P.
     */
    @PostMapping
    public ResponseEntity<RestaurantDto> register(@RequestBody @Valid RestaurantForm restaurantForm,
                                                  UriComponentsBuilder uriComponentsBuilder){
        Restaurant restaurant = restaurantForm.convertToRestaurant(restaurantForm);
        restaurantRepository.save(restaurant);

        URI uri = uriComponentsBuilder.
                path("{id}").
                buildAndExpand(restaurant.getId()).
                toUri();

        return ResponseEntity.created(uri).body(new RestaurantDto(restaurant));
    }
}
