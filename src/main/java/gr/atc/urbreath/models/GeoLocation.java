package gr.atc.urbreath.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoLocation {

    private double latitude;

    private double longitude;

    private String address;
}
