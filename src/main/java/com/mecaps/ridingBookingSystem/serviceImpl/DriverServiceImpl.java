package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.exception.DriverAlreadyExistsException;
import com.mecaps.ridingBookingSystem.exception.DriverNotFoundException;
import com.mecaps.ridingBookingSystem.exception.UserNotFoundException;
import com.mecaps.ridingBookingSystem.repository.DriverRepository;
import com.mecaps.ridingBookingSystem.repository.UserRepository;
import com.mecaps.ridingBookingSystem.request.DriverRequest;
import com.mecaps.ridingBookingSystem.response.DriverResponse;
import com.mecaps.ridingBookingSystem.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DriverServiceImpl implements DriverService {

    final private DriverRepository driverRepository;
    final private UserRepository userRepository;

    @Autowired
    public DriverServiceImpl(DriverRepository driverRepository, UserRepository userRepository) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<?> createDriver(DriverRequest request) {
        Optional<Driver> existingLicenseNumber = driverRepository
                .findByLicenseNumber(request.getLicenseNumber());
        if(existingLicenseNumber.isPresent()){
            throw new DriverAlreadyExistsException
                    ("Driver with this LICENSE NUMBER is already exists");
        }

        Optional<Driver> existingVehicleNumber = driverRepository
                .findByVehicleNumber(request.getVehicleNumber());
        if (existingVehicleNumber.isPresent()){
            throw new DriverAlreadyExistsException
                    ("Driver with this VEHICLE NUMBER is already exists");
        }

        Driver driver = new Driver();
        driver.setUserId(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("USER NOT FOUND")));
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setVehicleNumber(request.getVehicleNumber());
        driver.setVehicleModel(request.getVehicleModel());
        Driver save = driverRepository.save(driver);

        DriverResponse driverResponse = new DriverResponse(save);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message","Driver created successfully",
                        "body",driverResponse,
                        "success","true"));
    }

    public ResponseEntity<?> getDriverById(Long id){
        Driver driver = driverRepository.findById(id)
                .orElseThrow(()-> new DriverNotFoundException("Driver not found with given id"));

        DriverResponse driverResponse = new DriverResponse(driver);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message","Driver found successfully",
                        "body",driverResponse,
                        "success", "true"));
    }

    public ResponseEntity<?> getAllDrivers(){
        List<Driver> driverList = driverRepository.findAll();
        List<DriverResponse> driverResponseList = driverList.stream()
                            .map(DriverResponse::new).toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Get all driver list successfully",
                        "body",driverResponseList,
                        "success", "true"));
    }

    public ResponseEntity<?> updateDriver(Long id, DriverRequest request){
        Optional<Driver> existingLicenseNumber = driverRepository
                .findByLicenseNumber(request.getLicenseNumber());
        if(existingLicenseNumber.isPresent()){
            throw new DriverAlreadyExistsException
                    ("Driver with this LICENSE NUMBER is already exists");
        }

        Optional<Driver> existingVehicleNumber = driverRepository
                .findByVehicleNumber(request.getVehicleNumber());
        if (existingVehicleNumber.isPresent()){
            throw new DriverAlreadyExistsException
                    ("Driver with this VEHICLE NUMBER is already exists");
        }
        Driver driver = driverRepository.findById(id)
                .orElseThrow(()-> new DriverNotFoundException("Driver not found with given id"));

        driver.setUserId(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("USER NOT FOUND")));
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setVehicleNumber(request.getVehicleNumber());
        driver.setVehicleModel(request.getVehicleModel());

        Driver save = driverRepository.save(driver);
        DriverResponse driverResponse = new DriverResponse(save);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message","Driver details updated successfully",
                        "body",driverResponse,
                        "success", "true"));
    }


    public ResponseEntity<?> deleteDriver(Long id){
        Driver driver = driverRepository.findById(id)
                .orElseThrow(()-> new DriverNotFoundException("Driver not found with given id : " + id));
        driverRepository.delete(driver);

        return ResponseEntity.ok("DELETED");
    }

}
