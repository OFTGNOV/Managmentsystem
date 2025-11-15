package VehicleAndRoutingModule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VehicleSchedule {
	  private String vehicleId;
	  private List<ScheduleSlot> scheduleSlots;

	    public VehicleSchedule(String vehicleId) {
	        this.vehicleId = vehicleId;
	        this.scheduleSlots = new ArrayList<>();
	    }
	    
	    //Check if the vehicle is available during a specific time period
	    public boolean isAvailable(LocalDateTime startTime, LocalDateTime endTime) {
	        for (ScheduleSlot slot : scheduleSlots) {
	            // Check for overlap: new slot overlaps if it starts before existing ends
	            // AND ends after existing starts
	            if (startTime.isBefore(slot.getEndTime()) && endTime.isAfter(slot.getStartTime())) {
	                return false; // Schedule conflict
	            }
	        }
	        return true;
	    }
	    
	    //Books a time slot for the vehicle (Assigns to a route)
	    public boolean bookSlot(String routeId, LocalDateTime startTime, LocalDateTime endTime) {
	        if (!isAvailable(startTime, endTime)) {
	            return false; // Cannot book - conflict exists
	        }

	        ScheduleSlot newSlot = new ScheduleSlot(routeId, startTime, endTime);
	        scheduleSlots.add(newSlot);
	        return true;
	    }

	    
	    //Cancels a scheduled slot for a route,
	    public void cancelSlot(String routeId) {
	        scheduleSlots.removeIf(slot -> slot.getRouteId().equals(routeId));
	    }
	    
	    // Gets all scheduled slots for this vehicle 
	    public List<ScheduleSlot> getScheduleSlots() {
	        return new ArrayList<>(scheduleSlots);
	    }
	    
	    //Gets the next available time slot
	    public LocalDateTime getNextAvailableTime() {
	        if (scheduleSlots.isEmpty()) {
	            return LocalDateTime.now();
	        }

	        // Find the latest end time
	        LocalDateTime latestEnd = LocalDateTime.now();
	        for (ScheduleSlot slot : scheduleSlots) {
	            if (slot.getEndTime().isAfter(latestEnd)) {
	                latestEnd = slot.getEndTime();
	            }
	        }
	        return latestEnd;
	    }
	    
	  
	    public String getVehicleId() { return vehicleId; }
	    
	    //Inner class representing a time slot in the vehicle schedule 
	    public static class ScheduleSlot {
	        private String routeId;
	        private LocalDateTime startTime;
	        private LocalDateTime endTime;

	        public ScheduleSlot(String routeId, LocalDateTime startTime, LocalDateTime endTime) {
	            this.routeId = routeId;
	            this.startTime = startTime;
	            this.endTime = endTime;
	        }

	        public String getRouteId() { return routeId; }
	        public LocalDateTime getStartTime() { return startTime; }
	        public LocalDateTime getEndTime() { return endTime; }

	        @Override
	        public String toString() {
	            return String.format("Route %s: %s to %s", routeId, startTime, endTime);
	        }
	    }
	    
}
