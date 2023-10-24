import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MetroTicketBookingSystemApplication {

    private static final Map<String, Ticket> tickets = new HashMap<>();

    private static class Ticket {
        private String id;
        private String startStation;
        private String endStation;
        private LocalDateTime expiryTime;
        private int numUses;

        public Ticket(String id, String startStation, String endStation, LocalDateTime expiryTime) {
            this.id = id;
            this.startStation = startStation;
            this.endStation = endStation;
            this.expiryTime = expiryTime;
            this.numUses = 0;
        }

        public String getId() {
            return id;
        }

        public String getStartStation() {
            return startStation;
        }

        public String getEndStation() {
            return endStation;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }

        public int getNumUses() {
            return numUses;
        }

        public void incrementNumUses() {
            this.numUses++;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MetroTicketBookingSystemApplication.class, args);
    }

    @GetMapping("/stations")
    public Map<String, Station> getStations() {
        // TODO: Read the JSON file containing information about the stations and the ticket price and populate the map.
        return null;
    }

    @PostMapping("/book-ticket")
    public Ticket bookTicket(@RequestBody BookTicketRequest request) {
        // Validate the request.
        if (request.getStartStation() == null || request.getEndStation() == null) {
            throw new IllegalArgumentException("Start station and end station must be specified.");
        }

        // Generate a ticket ID.
        String ticketId = UUID.randomUUID().toString();

        // Calculate the expiry time.
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(18);

        // Create a new ticket object.
        Ticket ticket = new Ticket(ticketId, request.getStartStation(), request.getEndStation(), expiryTime);

        // Store the ticket object in the map.
        tickets.put(ticketId, ticket);

        // Return the ticket object to the user.
        return ticket;
    }

    @GetMapping("/validate-ticket")
    public boolean validateTicket(@RequestParam String ticketId) {
        // Check if the ticket ID exists in the map.
        if (!tickets.containsKey(ticketId)) {
            return false;
        }

        // Check if the ticket has expired.
        Ticket ticket = tickets.get(ticketId);
        if (ticket.getExpiryTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Check if the ticket has already been used twice.
        if (ticket.getNumUses() >= 2) {
            return false;
        }

        // The ticket is valid.
        return true;
    }

    @PostMapping("/use-ticket")
    public void useTicket(@RequestParam String ticketId) {
        // Validate the ticket ID.
        if (!validateTicket(ticketId)) {
            throw new IllegalArgumentException("Invalid ticket ID.");
        }

        // Increment the number of times the ticket has been used.
        Ticket ticket = tickets.get(ticketId);
        ticket.incrementNumUses();
    }
}
