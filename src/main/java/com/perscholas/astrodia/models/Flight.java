package com.perscholas.astrodia.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE)
@Entity
@Table(name = "flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String flightCode;
    @NonNull
    Timestamp departing;
    @NonNull
    Timestamp arriving;
    Integer seatsAvailable;
    @NumberFormat(style= NumberFormat.Style.CURRENCY)
    Double pricePerSeat;
    @Column(updatable = false)
    Timestamp createdAt;
    Timestamp updatedAt;
    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launchPadId", nullable = false)
    @NonNull
    Pad launchPad;
    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arrivalPadId", nullable = false)
    @NonNull
    Pad arrivalPad;
    @JsonManagedReference
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shuttleId", nullable = false)
    Shuttle shuttle;
    @Transient
    Date departingDate;
    @Transient
    Date arrivingDate;
    @Transient
    Time departingTime;
    @Transient
    Time arrivingTime;

    @PrePersist
    protected void onCreate() {
        long now = System.currentTimeMillis();
        this.createdAt =  new Timestamp(now);
    }

    @PreUpdate
    protected void onUpdate() {
        long now = System.currentTimeMillis();
        this.updatedAt = new Timestamp(now);
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightCode='" + flightCode + '\'' +
                ", departing=" + departing +
                ", arriving=" + arriving +
                ", seatsAvailable=" + seatsAvailable +
                ", pricePerSeat=" + pricePerSeat +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", launchPad=" + launchPad +
                ", arrivalPad=" + arrivalPad +
                ", shuttle=" + shuttle +
                '}';
    }
    public void getArrivingTime() {
        this.arrivingTime = new Time(this.arriving.getTime());
    }
    public void getDepartingTime() {
        this.departingTime = new Time(this.departing.getTime());
    }
    public void getArrivingDate() {
        this.arrivingDate = new Date(this.arriving.getTime());
    }
    public void getDepartingDate() {
        this.departingDate = new Date(this.departing.getTime());
    }
}