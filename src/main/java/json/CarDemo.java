package json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.Car;

import java.io.File;
import java.io.IOException;

public class CarDemo {

    public static void main(String[] args) throws IOException {



        ObjectMapper objectMapper = new ObjectMapper();
        Car car = new Car("yellow", "renault");
        try {
            objectMapper.writeValue(new File("target/car.json"), car);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String carAsString = objectMapper.writeValueAsString(car);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Car test = objectMapper.readValue("target/car.json",Car.class);

        String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
            Car car1 = objectMapper.readValue(json, Car.class);

        String json1 = "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";
            Car car2 = objectMapper.readValue(json1, Car.class);

        boolean equals = car2.equals(car1);
        System.out.println(equals);

        String json2 = "{ \"color\" : \"Black\", \"type\" : \"FIAT\" }";
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(json2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String color = jsonNode.get("color").asText();
        // Output: color -> Black

    }
}
