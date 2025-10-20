package za.co.simplitate.hotelbooking.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.dtos.RoomTO;
import za.co.simplitate.hotelbooking.entities.Room;
import za.co.simplitate.hotelbooking.enums.RoomType;
import za.co.simplitate.hotelbooking.exceptions.NotFoundException;
import za.co.simplitate.hotelbooking.repositories.RoomsRepository;
import za.co.simplitate.hotelbooking.services.RoomService;
import za.co.simplitate.hotelbooking.util.GenericMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static za.co.simplitate.hotelbooking.Const.ROOM_NOT_FOUND;
import static za.co.simplitate.hotelbooking.Const.SUCCESS;
import static za.co.simplitate.hotelbooking.util.CommonUtil.validateDates;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomsRepository roomsRepository;


    private static final String  IMAGE_DIR = System.getProperty("user.dir") + "/product-image/";

    @Override
    public Response addRoom(RoomTO roomTO, MultipartFile imageFile) {
        log.info("addRoom: {}", roomTO);
        Room roomEntity = GenericMapper.mapToRoom(roomTO);
        if(imageFile != null) {

            String imagePath = null;
            try {
                imagePath = saveImage(imageFile);
            } catch (Exception e) {
                log.warn("addRoom: could not add room image");
            }
            roomEntity.setImageUrl(imagePath);
        }

        Room savedRoom = roomsRepository.save(roomEntity);
        log.info("addRoom: room saved with id={}", savedRoom.getId());
        return Response.builder()
                .status(201)
                .message("Room successfully added.")
                .build();
    }

    @Override
    public Response updateRoom(RoomTO roomTO, MultipartFile imageFile) {
        log.info("updateRoom: {}", roomTO);
        Room existingRoom = roomsRepository.findById(roomTO.id())
                .orElseThrow(() -> {
                    var message = String.format(ROOM_NOT_FOUND, roomTO.id());
                    log.warn(message);
                    return new NotFoundException(message);
                });

        if(imageFile != null && !imageFile.isEmpty()) {
            String imagePath = "";
            try {
                imagePath = saveImage(imageFile);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            existingRoom.setImageUrl(imagePath);
        }

        updateRoom(roomTO, existingRoom);
        roomsRepository.save(existingRoom);
        return Response.builder()
                .status(204)
                .message("Room successfully updated.")
                .build();
    }

    private static void updateRoom(RoomTO roomTO, Room existingRoom) {
        if(roomTO.roomNumber() != null) {
            existingRoom.setRoomNumber(roomTO.roomNumber());
        }
        if(roomTO.roomType() != null) {
            existingRoom.setRoomType(roomTO.roomType());
        }
        if(roomTO.pricePerNight() != null) {
            existingRoom.setPricePerNight(roomTO.pricePerNight());
        }
        if(roomTO.capacity() != null) {
            existingRoom.setCapacity(roomTO.capacity());
        }
        if(roomTO.description() != null) {
            existingRoom.setDescription(roomTO.description());
        }
        if(roomTO.imageUrl() != null) {
            existingRoom.setImageUrl(roomTO.imageUrl());
        }
    }

    @Cacheable(value = "allRooms")
    @Override
    public Response getAllRooms() {
        log.info("getAllRooms: ");
        List<RoomTO> roomTOList;
        List<Room> roomList = roomsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        if (!roomList.isEmpty()) {
            roomTOList = roomList.parallelStream()
                    .map(GenericMapper::mapToRoomTO)
                    .toList();
        } else {
            throw new NotFoundException("No rooms found!!");
        }
        return Response.builder()
                .status(200)
                .message(SUCCESS)
                .rooms(roomTOList)
                .build();
    }

    @Override
    public Response getRoomById(Long roomId) {
        log.info("getRoomById: roomId={}", roomId);
        Room existingRoom = roomsRepository.findById(roomId)
                .orElseThrow(() -> {
                    var message = String.format(ROOM_NOT_FOUND, roomId);
                    log.warn(message);
                    return new NotFoundException(message);
                });
        RoomTO roomTO = GenericMapper.mapToRoomTO(existingRoom);
        return Response.builder()
                .status(200)
                .message(SUCCESS)
                .room(roomTO)
                .build();
    }

    @Override
    public Response deleteRoom(Long roomId) {
        log.info("deleteRoom: roomId={}", roomId);
        Room existingRoom = roomsRepository.findById(roomId)
                .orElseThrow(() -> {
                    var message = String.format(ROOM_NOT_FOUND, roomId);
                    log.warn(message);
                    return new NotFoundException(message);
                });
        roomsRepository.delete(existingRoom);
        return Response.builder()
                .status(204)
                .message("room deleted successfully")
                .build();
    }

    @Override
    public Response getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType) {
        log.info("getAvailableRooms: checkInDate={} checkOutDate={} roomType={}", checkInDate, checkOutDate, roomType);
        validateDates(checkInDate, checkOutDate);
        List<Room> roomList = roomsRepository.findAvailableRooms(roomType);
//        List<Room> roomList = roomsRepository.findAvailableRooms(checkInDate, checkOutDate, roomType);

        List<RoomTO> roomTOList;
        if (!roomList.isEmpty()) {
            roomTOList = roomList.parallelStream()
                    .map(GenericMapper::mapToRoomTO)
                    .toList();
        } else {
            throw new NotFoundException("No rooms found!!");
        }
        return Response.builder()
                .status(200)
                .message(SUCCESS)
                .rooms(roomTOList)
                .build();
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        log.info("getAllRoomTypes: ");
        return Arrays.asList(RoomType.values());
    }

    @Override
    public Response searchRoom(String input) {
        log.info("searchRoom: input={}", input);
        List<Room> roomList = roomsRepository.findByDescription(input);
        List<RoomTO> roomTOList;
        if (!roomList.isEmpty()) {
            roomTOList = roomList.parallelStream()
                    .map(GenericMapper::mapToRoomTO)
                    .toList();
        } else {
            throw new NotFoundException("No rooms found!!");
        }
        return Response.builder()
                .status(200)
                .message(SUCCESS)
                .rooms(roomTOList)
                .build();
    }

    @Override
    public Response getRoomsByType(RoomType roomType) {
        log.info("getRoomsByType: roomType={}", roomType);

        List<Room> roomList = roomsRepository.findRoomByRoomType(roomType);
        List<RoomTO> roomTOList;
        if (!roomList.isEmpty()) {
            roomTOList = roomList.parallelStream()
                    .map(GenericMapper::mapToRoomTO)
                    .toList();
        } else {
            throw new NotFoundException("No rooms found!!");
        }
        return Response.builder()
                .status(200)
                .message(SUCCESS)
                .rooms(roomTOList)
                .build();
    }

    private String saveImage(MultipartFile imageFile) throws Exception {
        log.info("saveImage: ");
        if (!imageFile.getContentType().startsWith("image/")) {
            log.error("saveImage: not an image");
           throw new IllegalArgumentException("Only image file allowed!!");
        }

        // create directory if it doesn't exist
        File dir = new File(IMAGE_DIR);
        if(!dir.exists()) {
            if (dir.mkdir()) {
                log.debug("Directory created");
            } else {
                log.warn("Could not create directory");
            }
        }

        //generate unique filename for image
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        String imagePath = IMAGE_DIR + fileName;

        try {
            File destinationFile = new File(imagePath);
            imageFile.transferTo(destinationFile);
        } catch (IOException | IllegalArgumentException ex) {
            log.error("saveImage: error while saving an image");
            throw new IllegalArgumentException(ex.getMessage());
        }

        return imagePath;
    }
}
