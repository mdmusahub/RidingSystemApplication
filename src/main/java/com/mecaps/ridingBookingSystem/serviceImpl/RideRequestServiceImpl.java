package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.*;
import com.mecaps.ridingBookingSystem.exception.RideRequestNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.repository.DriverRepository;
import com.mecaps.ridingBookingSystem.repository.LocationRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import com.mecaps.ridingBookingSystem.service.RideRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.mecaps.ridingBookingSystem.util.DistanceCalculator.calculateDistance;

@Service
public class RideRequestServiceImpl implements RideRequestService {

    private final static Double FARE_PER_KM = 10.00;

    private final RideRequestsRepository rideRequestsRepository;
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;
    private final LocationRepository locationRepository;

    public RideRequestServiceImpl(RideRequestsRepository rideRequestsRepository,
                                  RiderRepository riderRepository,
                                  DriverRepository driverRepository, LocationRepository locationRepository) {
        this.rideRequestsRepository = rideRequestsRepository;
        this.riderRepository = riderRepository;
        this.driverRepository = driverRepository;
        this.locationRepository = locationRepository;
    }

    // Start the ride request process.

    //  STEP - 1       CREATE RIDE REQUEST GIVE YOUR RIDER ID, PICKUP LAT&LNG, DROP LAT&LNG

    @Override
    public ResponseEntity<?> createRideRequest(RideRequestsDTO request) {
        Rider rider = riderRepository.findById(request.getRiderId())
                .orElseThrow(() -> new RiderNotFoundException
                        ("Rider not found with ID: " + request.getRiderId()));

        RideRequests rideRequest = new RideRequests();

        rideRequest.setRiderId(rider);
        rideRequest.setPickupLat(request.getPickupLat());
        rideRequest.setPickupLng(request.getPickupLng());
        rideRequest.setDropLat(request.getDropLat());
        rideRequest.setDropLng(request.getDropLng());

        rideRequestsRepository.save(rideRequest);

        Double distance = calculateDistance(request.getPickupLat(), request.getPickupLng(),
                request.getDropLat(), request.getDropLng());
        Double fare = distance * FARE_PER_KM;

        Map<String, Object> response = new HashMap<>();

        response.put("Message", "Ride request created successfully");
        response.put("rideRequestId", rideRequest.getId());
        response.put("distanceInKM", distance);
        response.put("estimatedFare", fare);
        response.put("success", true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // ON THIS STAGE WE CREATED OUR RIDE REQUEST AND WE GET OUR (MESSAGE, RIDE REQUEST ID,
    // DISTANCE BETWEEN LOCATIONS, FARE AS 10RS. PER KM),  LET'S GO TO THE NEXT STEP.




   // STEP - 2     ON THIS STEP WE WILL CONFIRM OUR RIDE REQUEST EITHER WE WANT TO GO OR NOT,
   // DECIDING ON THE BASIS OF DISTANCE AND FARE. IF RIDER ENTER YES YOUR RIDE REQUEST WILL GENERATE
    // OR IF RIDER ENTER NO RIDE REQUEST WILL GET CANCELED.

    @Override
    public ResponseEntity<?> confirmRideRequest(Long rideRequestId, Boolean confirmation) {
        RideRequests rideRequest = rideRequestsRepository.findById(rideRequestId)
                .orElseThrow(() -> new RideRequestNotFoundException("No Such Ride Request Found"));

        if (confirmation) {
            rideRequest.setStatus(RideStatus.REQUESTED);
            rideRequest.setRequestedAt(LocalDateTime.now().toString());
            rideRequest.setExpiresAt(LocalDateTime.now().plusMinutes(3).toString());

            rideRequestsRepository.save(rideRequest);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                    "message","Ride Request Confirmed",
                    "body",rideRequest,
                    "success",true
            ));
        } else {
            rideRequestsRepository.deleteById(rideRequestId);
            return ResponseEntity.ok("Ride Request Avoided");
        }
    }
    // AFTER CREATE THE RIDE REQUEST WE HAVE TO WAIT FOR 3 MIN.
    // IF APPLICATION FOUND THE DRIVER UNDER 1KM RADIUS OF RIDER ONLY THEN THE RIDE WILL START OTHERWISE
    // THE REQUEST WILL AUTOMATICALLY GET EXPIRED.


    //   STEP - 3    FROM HERE OUR RIDER'S WORK IS COMPLETE NOW OUR APPLICATION WILL FIND 3 NEAREST
    //   DRIVERS TO THE RIDER. IF FIRST DRIVER REJECT THE RIDE REQUEST AUTOMATICALLY PASS TO THE NEXT
    //   DRIVER AND IF HE REJECT THEN PASS TO THE THIRD DRIVER FINALLY IF HE REJECT THEN THE RIDE
    //   REQUEST WILL GOT COMPLETELY CANCEL AND A MESSAGE SENT TO RIDER
    //   "NO DRIVER FOUND NEAR YOUR LOCATION." .
    @Override
    public ResponseEntity<?> findNearestAvailableDrivers(RideRequestsDTO request, Integer limit) {

        Rider rider = riderRepository.findById(request.getRiderId())
                .orElseThrow(()->new RiderNotFoundException("Rider nit found."));

        List<Driver> availableDrivers = driverRepository.findByIsAvailableTrue();

        if (availableDrivers.isEmpty()) {
           return ResponseEntity.ok(Map.of("message","Sorry for the inconvenience," +
                    " no driers are available near your location."
           ,"success", false));
        }

        Map<Driver, Double> driverDistanceMap = new HashMap<>();

        for (Driver driver : availableDrivers) {
            Location driverLocation = locationRepository.findByDriverId(driver).orElse(null);
            if (driverLocation != null) {
                double distance = calculateDistance(request.getPickupLat(), request.getPickupLng(),
                        driverLocation.getLatitude(), driverLocation.getLongitude());
                driverDistanceMap.put(driver, distance);
            }
        }
           List<Driver> nearestDrivers = driverDistanceMap.entrySet().stream()
                   .sorted(Map.Entry.comparingByValue()).limit(limit).map(Map.Entry::getKey)
                   .toList();

           if (nearestDrivers.isEmpty()){
               return ResponseEntity.ok(Map.of(
                       "message", "No nearby drivers found.",
                       "success", false));
           }


           RideRequests rideRequests = rideRequestsRepository.findById(request.getRideRequestId())
                   .orElseThrow(()-> new RideRequestNotFoundException("Ride request not found."));

           boolean driverAssigned = false;
           Driver assignedDriver = null;

           for (Driver driver : nearestDrivers){

               boolean driverAccepts = simulateDriverResponse(driver);

               if (driverAccepts){
                   rideRequests.setStatus(RideStatus.ACCEPTED);
                   rideRequests.setAssignedDriver(driver);
                   rideRequestsRepository.save(rideRequests);

                   driverAssigned = true;
                   assignedDriver = driver;

                   break;
               }
           }

           if (!driverAssigned){
               rideRequests.setStatus(RideStatus.CANCELLED);
               rideRequestsRepository.save(rideRequests);

               return ResponseEntity.ok(Map.of("message","No driver found near your location."
               ,"success", false));
           }

           return ResponseEntity.ok(Map.of("message","Ride assigned successfully"
           ,"driverId",assignedDriver.getId(),
                   "vehicleNumber", assignedDriver.getVehicleNumber()
           ,"vehicleNumver", assignedDriver.getVehicleModel(),
           "status", rideRequests.getStatus()
           ,"success", true
           ));
        }

        // NOW THE RIDER'S AND APPLICATION'S WORK IS COMPLETE NEXT PROCESSES ARE FOR DRIVER.




    // A HELPER METHOD ACT LIKE A DRIVER THROW RANDOM ACCEPT/REJECT RESPONSE.
        public boolean simulateDriverResponse(Driver driver){

        Random random = new Random();
        return random.nextBoolean();
        }
}

