package edu.tamu.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.tamu.app.dto.MapDetail;
import edu.tamu.app.enums.MapType;

/**
 * The MapService returns record location details
 *
 * @author Jason Savell <jsavell@library.tamu.edu>
 *
 */

@Service
@ConfigurationProperties("mapDetails")
@PropertySource("classpath:mapDetails.properties")
public class MapService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("#{'${mapDetails.stackMap}'.split(';')}")
    private List<String> stackMapLocations;

    @Value("#{'${mapDetails.bypassCodes}'.split(';')}")
    private List<String> bypassCodes;

    @Value("${mapDetails.defaultMapLink}")
    private String defaultMapLink;

    public final Map<String, String> mapLinks = new HashMap<String,String>();

    public Map<String, String> getMapLinks() {
        return mapLinks;
    }

    public MapDetail getMapLink(String location) {
        String locationPrefix = location.split(",")[0];
        MapDetail mapDetail = new MapDetail();

        if (bypassCodes.contains(location)) {
            mapDetail = buildUnknownMapDetail();
        } else if (stackMapLocations.contains(locationPrefix)) {
            mapDetail.type = MapType.StackMap;
        } else {
            if (getMapLinks().containsKey(locationPrefix)) {
                mapDetail.type = MapType.URL;
                mapDetail.url = getMapLinks().get(locationPrefix);
            } else {
                mapDetail = buildUnknownMapDetail();
            }
        }
        return mapDetail;
    }

    private MapDetail buildUnknownMapDetail() {
        MapDetail mapDetail = new MapDetail();
        mapDetail.type = MapType.Unknown;
        mapDetail.url = defaultMapLink;
        return mapDetail;
    }
}
