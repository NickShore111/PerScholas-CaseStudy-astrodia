package com.perscholas.astrodia.Util;

import com.perscholas.astrodia.models.Flight;
import com.perscholas.astrodia.models.Pad;
import com.perscholas.astrodia.models.Shuttle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
public class FlightCreator {
    private final List<Shuttle> shuttles;
    private final List<Pad> pads;
    private final Random rand = new Random();
//    default window of 30 days for flight departure creation
    private int departureWindow = 30;

    private List<Flight> flightsList = new ArrayList<>();

    public FlightCreator(List<Shuttle> shuttles, List<Pad> pads) {
        this.shuttles = shuttles;
        this.pads = pads;
    }
    public FlightCreator(List<Shuttle> shuttles, List<Pad> pads, int departureWindow) {
        this.shuttles = shuttles;
        this.pads = pads;
        this.departureWindow = departureWindow;
    }

    public List<Flight> getListOfFlights(int numberOfFlights) {
        for (int i = 0; i < numberOfFlights; i++) {
            Flight f = this.createNewFlight();
            flightsList.add(f);
        }
        return flightsList;
    }
    private String createFlightCode(
            Calendar departure,
            String shuttleSpacelinerId,
            String launchPadId,
            String arrivalPadId) {
        int day = departure.get(Calendar.DAY_OF_YEAR);
        return String.format("%s%d %s-%s", shuttleSpacelinerId, day, launchPadId, arrivalPadId);
    }

    private Flight createNewFlight() {
        Calendar cal = this.getFutureDeparture();
        // set timestamp object to new timestamp using future cal object
        Timestamp futureDeparture = new Timestamp(cal.getTime().getTime());
        int randomShuttleIdx = rand.nextInt(shuttles.size());
        int launchPadIdx = rand.nextInt(pads.size());
        int arrivalPadIdx = this.getArrivalPadIdx(launchPadIdx);
        Pad launchPad = pads.get(launchPadIdx);
        Pad arrivalPad = pads.get(arrivalPadIdx);
        // get future arrival time relative to location traveling to
        Timestamp futureArrival = this.getFutureArrival(futureDeparture, launchPad, arrivalPad);
        Shuttle shuttle = shuttles.get(randomShuttleIdx);
        String flightCode = this.createFlightCode(cal, shuttle.getSpaceliner().getId(), launchPad.getId(), arrivalPad.getId());

        Flight newFlight = new Flight();
        newFlight.setFlightCode(flightCode);
        newFlight.setDeparting(futureDeparture);
        newFlight.setArriving(futureArrival);
        newFlight.setLaunchPad(launchPad);
        newFlight.setArrivalPad(arrivalPad);
        newFlight.setShuttle(shuttle);
        newFlight.setSeatsAvailable(shuttle.getPassengerCapacity());

        return newFlight;
    }
    private Calendar getFutureDeparture() {
        // add random days, minute, hour to departure timestamp
        int randDay = rand.nextInt(departureWindow);
        int randHour = rand.nextInt(24);
        int randMinute = rand.nextInt(12) * 5;
        // create new timestamp object with current time
        Timestamp timestamp = new Timestamp(new Date().getTime());
        // create cal object instance
        Calendar cal = Calendar.getInstance();
        // set calendar object to now
        cal.setTimeInMillis(timestamp.getTime());
        // add random values to cal object
        cal.add(Calendar.DAY_OF_MONTH, randDay);
        cal.add(Calendar.HOUR, randHour);
        cal.set(Calendar.MINUTE, 0);
        cal.add(Calendar.MINUTE, randMinute);
        return cal;
    }
    private Timestamp getFutureArrival(Timestamp departureTimestamp, Pad launchPad, Pad arrivalPad) {
        String fromRegion = launchPad.getPort().getRegion().getId();
        String toRegion = arrivalPad.getPort().getRegion().getId();
        int randHour = 0;
        // Earth surface <to> Earth Orbit
        if (fromRegion.equals("ES") && toRegion.equals("EO") ||
                fromRegion.equals("EO") && toRegion.equals("ES")) {
            randHour = rand.nextInt(66) + 6; // 6 hours to 3 days
        } else if// Earth surface <to> Moon
            (fromRegion.equals("ES") && toRegion.equals("MO") ||
                fromRegion.equals("MO") && toRegion.equals("ES")) {
            randHour = rand.nextInt(12) + 66; // 3 days on average, 12 hour window
        } else if// Earth surface <to> Mars
            (fromRegion.equals("ES") && toRegion.equals("MA") ||
                fromRegion.equals("MA") && toRegion.equals("ES")) {
            randHour = rand.nextInt(593) + 93; // from 93 hours to 686 hours(farthest approach)
        } else if// Earth orbit <to> Moon
            (fromRegion.equals("EO") && toRegion.equals("MO") ||
                fromRegion.equals("MO") && toRegion.equals("EO")) {
            randHour = rand.nextInt(24) + 24; // 1 to 2 days
        } else if// Earth orbit <to> Mars
            (fromRegion.equals("EO") && toRegion.equals("MA") ||
                fromRegion.equals("MA") && toRegion.equals("EO")) {
            randHour = rand.nextInt(545) + 87; // 6hours - 48hours shorter travel
        } else// Moon <to> Mars
         {
            randHour = rand.nextInt(545) + 80; //
        }
        int randMinute = rand.nextInt(60);
        Calendar cal = Calendar.getInstance();
        // set calendar object to departure time
        cal.setTimeInMillis(departureTimestamp.getTime());
        // add random time values to cal object
        cal.add(Calendar.MINUTE, randMinute);
        cal.add(Calendar.HOUR, randHour);
        // set timestamp object to new timestamp using future cal object
        return new Timestamp(cal.getTime().getTime());
    }

    private int getArrivalPadIdx(int launchPadIdx) {
        int randomIdx = rand.nextInt(pads.size());
        // avoid creating flights travelling to and from same region
        while (pads.get(randomIdx).getPort().getRegion() ==
                pads.get(launchPadIdx).getPort().getRegion()) {
            randomIdx = rand.nextInt(pads.size());
        }
        return randomIdx;
    }
}
