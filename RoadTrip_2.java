import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

class RoadTrip_2 {
    private HashMap<String, String> attractions = new HashMap<>();
    private HashMap<String, ArrayList<String>> locations = new HashMap<>();
    private HashMap<String, HashMap<String, RoadTripCost>> graph = new HashMap<>();

    public RoadTrip_2(String attraction_file, String roads_file) throws FileNotFoundException {
        File file = new File(attraction_file);
        Scanner reader = new Scanner(file);
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] data = line.split(",");
            String attraction = data[0].trim();
            String location = data[1].trim();
            attractions.put(attraction, location);
            if (!locations.containsKey(location)) {
                locations.put(location, new ArrayList<String>());
            }
            if (!locations.get(location).contains(attraction)) {
                locations.get(location).add(attraction);
            }
        }
        reader.close();
        file = new File(roads_file);
        reader = new Scanner(file);
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] data = line.split(",");
            String location1 = data[0].trim();
            String location2 = data[1].trim();
            Integer miles = Integer.parseInt(data[2]);
            Integer minutes = Integer.parseInt(data[3]);
            if (!graph.containsKey(location1)) {
                graph.put(location1, new HashMap<String, RoadTripCost>());
            }
            if (!graph.containsKey(location2)) {
                graph.put(location2, new HashMap<String, RoadTripCost>());
            }
            if (!graph.get(location1).containsKey(location2)) {
                graph.get(location1).put(location2, new RoadTripCost(miles, minutes));
            }
            if (!graph.get(location2).containsKey(location1)) {
                graph.get(location2).put(location1, new RoadTripCost(miles, minutes));
            }
        }
        reader.close();
    }

    public static double relevance(List<String> left, List<String> right){
        double result = 0;
        for (String element: right){
            if (left.contains(element)){
                result++;
            }

        }
        return result/right.size();
    }

    public List<String> route(String startingCity, String endingCity) {
        List<String> result = new ArrayList<>();
        HashMap<String, String> prev = new HashMap<>();
        HashMap<String, Integer> dist = new HashMap<>();
        ArrayList<String> Q = new ArrayList<>();
        for (String vertex : graph.keySet()) {
            dist.put(vertex, null);
            prev.put(vertex, null);
            Q.add(vertex);
        }
        dist.replace(startingCity, 0);
        while (Q.size() > 0) {
            Integer min = null;
            String min_u = null;
            for (String u : Q) {
                if (dist.get(u) != null) {
                    if (min == null || dist.get(u) < min) {
                        min = dist.get(u);
                        min_u = u;
                    }
                }
            }
            Q.remove(min_u);
            for (String vertex : graph.get(min_u).keySet()) {
                Integer alt = dist.get(min_u) + graph.get(min_u).get(vertex).miles;
                if (dist.get(vertex) == null || alt < dist.get(vertex)) {
                    dist.replace(vertex, alt);
                    prev.replace(vertex, min_u);
                }
            }
        }
        String u = endingCity;
        do {
            result.add(0, u);
            u = prev.get(u);
        } while (!u.equals(startingCity));

        return result;
    }

    public List<String> route(String startingCity, String endingCity, List<String> attractions) {
        List<String> result = new ArrayList<>();
        int middle = 0;
        result.add(middle++, startingCity);
        result.add(middle, endingCity);
        ArrayList<String> places = new ArrayList<>();
        for (String attraction: attractions){
            String place = this.attractions.get(attraction);
            if (!result.contains(place) && !places.contains(place)){
                places.add(place);
            }
        }
        while (places.size() > 0){
            List<String> best = null;
            for (String place: places){
                List<String> attempt = route(result.get(middle-1), place);
                // if (attempt.contains(endingCity)){
                //     continue;
                // }
                if (best == null || relevance(places, attempt) > relevance (places, best)){
                    best = attempt;
                }
            }
            best.remove(result.get(middle-1));
            for (String place: best ){
                places.remove(place);
                result.add(middle++, place);
            }
        }
        List<String> rest = route(result.get(middle-1), endingCity);
        rest.remove(result.get(middle-1));
        rest.remove(endingCity);
        for (String place: rest){
            result.add(middle++, place);
        }
        return result;
    }

    public static void main(String[] args) {
        RoadTrip_2 map = null;
        try {
            map = new RoadTrip_2("attractions.csv", "roads.csv");
        } catch (FileNotFoundException e) {
            System.out.println("unable to open attractions.csv");
            e.printStackTrace();
        }
        List<String> attractions = new ArrayList<>();
        attractions.add("Las Vegas Strip");
        attractions.add("Albuquerque Sunrise Balloon Rides");
        attractions.add("Grand Canyon");
        System.out.println(map.route("Bakersfield CA", "Abilene TX", attractions));
        // try two puts; first element key second destination
        // for instance, graph.put(data[0].trim(), new Destination(data[1].trim(),
        // Integer.parseInt(data[2]), Integer.parseInt(data[3])))

    }

}